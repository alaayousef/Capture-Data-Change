package gov.eehc.holding.client.common;

import gov.eehc.holding.client.repositories.lastrun.boundry.LastRunDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.logging.Logger;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

/**
 * @author Mawaziny
 */
@Service
@Scope(SCOPE_SINGLETON)
public class GlobalConfiguration {

    private static final Logger LOGGER = Logger.getGlobal();
    private Integer sourceCompany;
    private LastRunDateRepository lastRunDateRepository;

    @Autowired
    public GlobalConfiguration(LastRunDateRepository lastRunDateRepository) {
        this.lastRunDateRepository = lastRunDateRepository;
    }

    @PostConstruct
    public void initConfiguration() {
        sourceCompany = Integer.valueOf(lastRunDateRepository.getSourceCompany());
        LOGGER.info("SOURCE_COMPANY: " + sourceCompany);
    }

    public Integer getSourceCompany() {
        return sourceCompany;
    }
}
