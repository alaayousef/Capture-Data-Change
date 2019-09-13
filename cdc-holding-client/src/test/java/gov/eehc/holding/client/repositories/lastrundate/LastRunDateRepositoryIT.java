package gov.eehc.holding.client.repositories.lastrundate;

import gov.eehc.holding.client.repositories.lastrun.boundry.LastRunDateRepository;
import gov.eehc.holding.client.repositories.lastrun.entity.LastRunDate;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static gov.eehc.holding.client.common.Constants.DATE_TIME_FORMATTER;
import static org.junit.Assert.assertThat;

/**
 * @author aabdelrahman
 */
@DataJpaTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class LastRunDateRepositoryIT {

    @Autowired
    private LastRunDateRepository lastRunDateRepository;

    public LastRunDateRepositoryIT() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of findTopOneByTableNameOrderByEndDate method, of class
     * CdcProcessRepository.
     */
    @Test
    public void testFindTopOneByTableNameOrderByEndDate() {
        System.out.println("findTopOneByTableNameOrderByEndDate");
        String tableName = "tableName1";
        LastRunDate firstLastRunDate = new LastRunDate();
        LastRunDate secondLastRunDate = new LastRunDate();
        LocalDateTime localDateTime = LocalDateTime.parse("2014-12-15 23:59:48.000", DATE_TIME_FORMATTER);
        LocalDateTime secondLocalDateTime = LocalDateTime.parse("2015-12-15 23:59:48.000", DATE_TIME_FORMATTER);
        firstLastRunDate.setEndDate(localDateTime);
        secondLastRunDate.setEndDate(secondLocalDateTime);
        firstLastRunDate.setTableName("tableName1");
        secondLastRunDate.setTableName("tableName1");
        lastRunDateRepository.save(firstLastRunDate);
        lastRunDateRepository.save(secondLastRunDate);
        LastRunDate result = lastRunDateRepository.findTopOneByTableNameOrderByEndDate(tableName);
        assertThat(result.getId(), Is.is(secondLastRunDate.getId()));
    }

}
