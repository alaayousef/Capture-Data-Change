package gov.eehc.holding.client.schedulers;

import gov.eehc.holding.client.business.cdc.boundary.CdcService;
import gov.eehc.holding.client.common.GlobalConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static gov.eehc.holding.client.common.Constants.OP_DELETE;
import static gov.eehc.holding.client.common.Constants.OP_INSERT;
import static gov.eehc.holding.client.common.Constants.OP_UPDATE_AFTER;
import static gov.eehc.holding.client.common.Constants.PAGE_SIZE;

/**
 * @author aabdelrahman
 */
@Component
public class CdcScheduler {

    private CdcService cdcService;
    private GlobalConfiguration configuration;

    private int[] operations = {OP_INSERT, OP_DELETE, OP_UPDATE_AFTER};

    @Autowired
    public CdcScheduler(CdcService cdcService, GlobalConfiguration configuration) {
        this.cdcService = cdcService;
        this.configuration = configuration;
    }

    @Scheduled(fixedDelay = 4 * 60 * 60 * 1000)
    public void logCdcChanges() {
        cdcService.getChangesWithPaginationAfterLastRunDate(PAGE_SIZE, operations, configuration.getSourceCompany());
    }
}
