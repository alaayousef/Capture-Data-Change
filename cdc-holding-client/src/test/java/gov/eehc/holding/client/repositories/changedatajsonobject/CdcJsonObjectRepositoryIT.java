package gov.eehc.holding.client.repositories.changedatajsonobject;

import gov.eehc.holding.client.repositories.cdcjsonobject.boundry.CdcJsonObjectRepository;
import gov.eehc.holding.client.repositories.cdcjsonobject.entity.CdcJsonObject;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertThat;

/**
 *
 * @author aabdelrahman
 */
@DataJpaTest
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CdcJsonObjectRepositoryIT {

    @Autowired
    private CdcJsonObjectRepository cdcJsonObjectRepository;

    public CdcJsonObjectRepositoryIT() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        System.out.println("Clearing all data in the repository before starting a new test");
        cdcJsonObjectRepository.deleteAll();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of findWhereSuccessfulSendDateIsNULL method, of class
     * CdcJsonObjectRepository.
     */
    @Test
    public void testFindAllWhereSuccesfullsendDateIsNULL() {
        System.out.println("findWhereSuccessfulSendDateIsNULL");
        CdcJsonObject cdcJsonObjectWithoutSendDate = new CdcJsonObject();
        cdcJsonObjectWithoutSendDate.setCreationDate(LocalDateTime.now());
        cdcJsonObjectRepository.save(cdcJsonObjectWithoutSendDate);
        CdcJsonObject cdcJsonObjectWithSendDate = new CdcJsonObject();
        cdcJsonObjectWithSendDate.setCreationDate(LocalDateTime.now());
        cdcJsonObjectWithSendDate.setSuccessfulSendDate(LocalDateTime.now());
        cdcJsonObjectRepository.save(cdcJsonObjectWithSendDate);
        List<CdcJsonObject> result = cdcJsonObjectRepository.findWhereSuccessfulSendDateIsNULL(new PageRequest(0, 5));
        assertThat(result.size(), Is.is(1));
    }

    /**
     * Test of findWhereSuccessfulSendDateIsNULL method, of class
     * CdcJsonObjectRepository.
     */
    @Test
    public void testDeleteAllWhereSuccesfullSendDateIsNotNull() {
        CdcJsonObject cdcJsonObjectWithoutSendDate = new CdcJsonObject();
        cdcJsonObjectWithoutSendDate.setCreationDate(LocalDateTime.now());
        cdcJsonObjectRepository.save(cdcJsonObjectWithoutSendDate);
        CdcJsonObject cdcJsonObjectWithSendDate = new CdcJsonObject();
        cdcJsonObjectWithSendDate.setCreationDate(LocalDateTime.now());
        cdcJsonObjectWithSendDate.setSuccessfulSendDate(LocalDateTime.now());
        cdcJsonObjectRepository.save(cdcJsonObjectWithSendDate);
        List<CdcJsonObject> result = cdcJsonObjectRepository.findAll();
        assertThat(result.size(), Is.is(2));
        cdcJsonObjectRepository.deleteWhereSuccessfulSendDateIsNotNull();
        result = cdcJsonObjectRepository.findAll();
        assertThat(result.size(), Is.is(1));
    }
}
