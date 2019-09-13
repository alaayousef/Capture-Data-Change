package gov.eehc.holding.client.schedulers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.eehc.holding.client.business.cdc.boundary.CdcService;
import gov.eehc.holding.client.repositories.cdcjsonobject.boundry.CdcJsonObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import static gov.eehc.holding.client.common.Constants.HOLDING_COMPANY_URL;
import static gov.eehc.holding.client.common.Constants.PAGE_SIZE;
import static gov.eehc.holding.client.common.Constants.SENDING_TRIALS;
import static org.springframework.http.HttpStatus.NO_CONTENT;

/**
 * @author aabdelrahman
 */
@Component
public class SenderScheduler {

    private Logger LOGGER = Logger.getGlobal();
    private final CdcService cdcService;
    private final CdcJsonObjectRepository cdcJsonObjectRepository;

    @Autowired
    public SenderScheduler(CdcService cdcService, CdcJsonObjectRepository cdcJsonObjectRepository) {
        this.cdcService = cdcService;
        this.cdcJsonObjectRepository = cdcJsonObjectRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendData() {
        int sendingTrials = 1;
        ObjectMapper mapper = new ObjectMapper();
        Collection<ObjectNode> jsonObjects = cdcService.getAllSendingData(PAGE_SIZE);
        boolean shouldSend = !jsonObjects.isEmpty();
        while (shouldSend && sendingTrials <= SENDING_TRIALS) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.putArray("changes").addAll(jsonObjects);
            RestTemplate template = new RestTemplate();
            HttpEntity<ObjectNode> request = new HttpEntity<>(objectNode);
            try {
                LOGGER.info("Sending: " + objectNode.toString());
                HttpStatus httpStatus = template.postForEntity(HOLDING_COMPANY_URL, request, String.class).getStatusCode();
                boolean succeeded = httpStatus.equals(NO_CONTENT);
                cdcService.logSentData(jsonObjects, succeeded);
                if (succeeded) {
                    jsonObjects = cdcService.getAllSendingData(PAGE_SIZE);
                    sendingTrials = 1;
                    shouldSend = !jsonObjects.isEmpty();
                } else {
                    sendingTrials++;
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                cdcService.logSentData(jsonObjects, false);
                sendingTrials++;
            }
        }
        cdcJsonObjectRepository.deleteWhereSuccessfulSendDateIsNotNull();
    }
}
