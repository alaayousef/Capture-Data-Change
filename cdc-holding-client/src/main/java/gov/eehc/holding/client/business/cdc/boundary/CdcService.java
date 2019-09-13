package gov.eehc.holding.client.business.cdc.boundary;

import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.eehc.holding.client.business.cdc.control.CdcCtrl;
import gov.eehc.holding.client.repositories.cdcjsonobject.boundry.CdcJsonObjectRepository;
import gov.eehc.holding.client.repositories.cdcjsonobject.entity.CdcJsonObject;
import gov.eehc.holding.client.repositories.changedata.boundary.ChangeDataRepository;
import gov.eehc.holding.client.repositories.changedata.entity.CdcModel;
import gov.eehc.holding.client.repositories.changedata.entity.ChangeData;
import gov.eehc.holding.client.repositories.lastrun.boundry.LastRunDateRepository;
import gov.eehc.holding.client.repositories.lastrun.entity.LastRunDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author aabdelrahman
 */
@Service
public class CdcService {

    private Logger LOGGER = Logger.getGlobal();

    private final LastRunDateRepository lastRunDateRepository;
    private final CdcCtrl cdcCtrl;
    private final ChangeDataRepository changeDataRepository;
    private final CdcJsonObjectRepository cdcJsonObjectRepository;

    @Autowired
    public CdcService(LastRunDateRepository lastRunDateRepository, CdcCtrl cdcCtrl,
                      ChangeDataRepository changeDataRepository, CdcJsonObjectRepository cdcJsonObjectRepository) {
        this.lastRunDateRepository = lastRunDateRepository;
        this.cdcCtrl = cdcCtrl;
        this.changeDataRepository = changeDataRepository;
        this.cdcJsonObjectRepository = cdcJsonObjectRepository;
    }

    public void getChangesWithPaginationAfterLastRunDate(int pageSize, int[] operationNumbers, int sourceCompany) {
        Collection<ChangeData> changeData = changeDataRepository.getChangeData();

        changeData.forEach((cdc) -> {
            LastRunDate lastRunDate = new LastRunDate();
            lastRunDate.setStartDate(LocalDateTime.now());
            lastRunDate.setTableName(cdc.getCaptureInstance());
            LocalDateTime endDate = null;

            try {
                LastRunDate previousLastRunDate = lastRunDateRepository.findTopOneByTableNameOrderByEndDate(cdc.getCaptureInstance());

                LocalDateTime previousRunDate = cdcCtrl.getPreviousCdcRunDate(previousLastRunDate);

                Long changesCount = changeDataRepository.getChangesCountAfterLastRunDate(cdc, operationNumbers, previousRunDate);

                Long numberOfPages = cdcCtrl.calculateNumberOfPages(changesCount, pageSize);

                for (int pageNumber = 1; pageNumber <= numberOfPages; pageNumber++) {
                    int offset = cdcCtrl.calculateOffset(pageSize, pageNumber);

                    List<CdcModel> cdcModels = changeDataRepository.
                            getChangesAfterLastRunDate(cdc, offset, previousRunDate, operationNumbers, pageSize);

                    List<CdcJsonObject> cdcJsonObjects = cdcCtrl.getCdcJsonObjects(cdcModels, sourceCompany);

                    cdcJsonObjectRepository.saveAll(cdcJsonObjects);

                    if (pageNumber == numberOfPages) {
                        endDate = cdcModels.get(cdcModels.size() - 1).getCreationDate();
                    }
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            } finally {
                if (endDate == null) {
                    lastRunDate.setEndDate(LocalDateTime.now());
                } else {
                    lastRunDate.setEndDate(endDate);
                }
                lastRunDateRepository.save(lastRunDate);
            }
        });

    }

    public Collection<ObjectNode> getAllSendingData(int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page Size Must be greater than 0");
        }

        try {
            List<CdcJsonObject> sendingData = cdcJsonObjectRepository.
                    findWhereSuccessfulSendDateIsNULL(PageRequest.of(0, pageSize));
            return cdcCtrl.prepareDataBeforeSending(sendingData);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public void logSentData(Collection<ObjectNode> jsonObjects, boolean success) {
        if (jsonObjects == null || jsonObjects.isEmpty()) {
            throw new IllegalArgumentException("json objects list size must be greater than 0");
        }

        if (success) {
            Collection<Long> jsonObjectIds = cdcCtrl.extractJsonObjectIds(jsonObjects);
            cdcJsonObjectRepository.updateSentJsonObjects(LocalDateTime.now(), new ArrayList<>(jsonObjectIds));
            LOGGER.info("Successful sent: " + jsonObjects.toString());
        } else {
            LOGGER.severe("Cannot send: " + jsonObjects.toString());
        }

    }

}
