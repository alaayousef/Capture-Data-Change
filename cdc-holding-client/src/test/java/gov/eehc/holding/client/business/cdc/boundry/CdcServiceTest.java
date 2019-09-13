package gov.eehc.holding.client.business.cdc.boundry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.eehc.holding.client.business.cdc.boundary.CdcService;
import gov.eehc.holding.client.business.cdc.control.CdcCtrl;
import gov.eehc.holding.client.repositories.cdcjsonobject.boundry.CdcJsonObjectRepository;
import gov.eehc.holding.client.repositories.cdcjsonobject.entity.CdcJsonObject;
import gov.eehc.holding.client.repositories.changedata.boundary.ChangeDataRepository;
import gov.eehc.holding.client.repositories.changedata.entity.CdcModel;
import gov.eehc.holding.client.repositories.changedata.entity.ChangeData;
import gov.eehc.holding.client.repositories.lastrun.boundry.LastRunDateRepository;
import gov.eehc.holding.client.repositories.lastrun.entity.LastRunDate;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static gov.eehc.holding.client.common.Constants.DATE_TIME_FORMATTER;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 *
 * @author aabdelrahman
 */
public class CdcServiceTest {

    @Mock
    private CdcJsonObjectRepository cdcJsonObjectRepository;
    @Mock
    private ChangeDataRepository changeDataRepository;
    @Mock
    private LastRunDateRepository lastRunDateRepository;

    private CdcCtrl cdcCtrl;

    private CdcService cdcService;

    public CdcServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        cdcCtrl = new CdcCtrl();
        lastRunDateRepository = Mockito.mock(LastRunDateRepository.class);
        changeDataRepository = Mockito.mock(ChangeDataRepository.class);
        cdcJsonObjectRepository = Mockito.mock(CdcJsonObjectRepository.class);
        cdcService = new CdcService(lastRunDateRepository, cdcCtrl, changeDataRepository, cdcJsonObjectRepository);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getChangesWithPaginationAfterLastRunDate method, of class
     * CdcBoundary.
     */
    @Test
    public void testGetChangesWithPaginationAfterLastRunDateForZeroCdcChanges() {
        System.out.println("getChangesWithPaginationAfterLastRunDate");
        int pageSize = 10;
        int[] operationNumber = {0};
        int sourceCompany = 0;
        long listCount = 0;

        Collection<ChangeData> changeDataList = new ArrayList<>();
        ChangeData changeData = new ChangeData();
        changeData.setCaptureInstance("tableNameTest");
        changeData.setColumnNames("testColumn1,testColumn2,testColumn3");
        changeDataList.add(changeData);

        LastRunDate lastRunDate = new LastRunDate();
        lastRunDate.setEndDate(LocalDateTime.now());
        lastRunDate.setTableName(changeData.getCaptureInstance());

        when(changeDataRepository.getChangeData()).thenReturn(changeDataList);
        when(changeDataRepository.getChangesCountAfterLastRunDate(changeData, operationNumber, lastRunDate.getEndDate())).thenReturn(listCount);
        when(lastRunDateRepository.save(any())).thenReturn(any());
        when(lastRunDateRepository.findTopOneByTableNameOrderByEndDate("tableNameTest")).thenReturn(lastRunDate);
        cdcService.getChangesWithPaginationAfterLastRunDate(pageSize, operationNumber, sourceCompany);
        Mockito.verify(lastRunDateRepository).save(Mockito.argThat(cdcProcessObject -> cdcProcessObject.getEndDate() != null));
    }

    @Test
    public void testGetChangesWithPaginationAfterLastRunDateForSettingNewCdcEndDateCorrectly() throws ParseException {
        System.out.println("getChangesWithPaginationAfterLastRunDate");
        int pageSize = 10;
        int[] operationNumber = {0};
        int sourceCompany = 0;
        long listCount = 5;

        Collection<ChangeData> changeDataList = new ArrayList<>();
        ChangeData changeData = new ChangeData();
        changeData.setCaptureInstance("dbo_tableNameTest");
        changeData.setColumnNames("testColumn1,testColumn2,testColumn3");
        changeDataList.add(changeData);

        List<CdcModel> changesResultList = new ArrayList<>();
        CdcModel cdcModel = new CdcModel();
        cdcModel.setCreationDate(LocalDateTime.parse("2018-12-31 23:59:59.993", DATE_TIME_FORMATTER));
        cdcModel.setOperation(String.valueOf(operationNumber[0]));
        cdcModel.setSchema("dbo");
        cdcModel.setTableName("tableNameTest");
        HashMap<String, String> tableChanges = new HashMap<>();
        tableChanges.put("testColumn1", "column 1 value");
        tableChanges.put("testColumn2", "column 2 value");
        tableChanges.put("testColumn3", "column 3 value");
        cdcModel.setTableChanges(tableChanges);
        changesResultList.add(cdcModel);
        LastRunDate lastRunDate = new LastRunDate();
        lastRunDate.setEndDate(LocalDateTime.now());
        lastRunDate.setTableName(changeData.getCaptureInstance());

        when(changeDataRepository.getChangeData()).thenReturn(changeDataList);
        when(changeDataRepository.getChangesCountAfterLastRunDate(changeData, operationNumber, lastRunDate.getEndDate())).thenReturn(listCount);
        int offset = cdcCtrl.calculateOffset(pageSize, 1);
        when(changeDataRepository.getChangesAfterLastRunDate(changeData, offset, lastRunDate.getEndDate(), operationNumber, pageSize)).thenReturn(changesResultList);
        when(lastRunDateRepository.save(any())).thenReturn(any());
        when(lastRunDateRepository.findTopOneByTableNameOrderByEndDate("tableNameTest")).thenReturn(lastRunDate);
        cdcService.getChangesWithPaginationAfterLastRunDate(pageSize, operationNumber, sourceCompany);
        Mockito.verify(cdcJsonObjectRepository).saveAll(any());
        Mockito.verify(lastRunDateRepository).save(Mockito.argThat(cdcProcessObject -> cdcProcessObject.getEndDate().compareTo(cdcModel.getCreationDate()) == 0));
    }

    @Test
    public void testGetChangesWithPaginationAfterLastRunDateForExceptionHandling() throws ParseException {
        System.out.println("getChangesWithPaginationAfterLastRunDate");
        int pageSize = 10;
        int[] operationNumber = {0};
        int sourceCompany = 0;
        int listCount = 5;

        Collection<ChangeData> changeDataList = new ArrayList<>();
        ChangeData changeData = new ChangeData();
        changeData.setCaptureInstance("tableNameTest");
        changeData.setColumnNames("testColumn1,testColumn2,testColumn3");
        changeDataList.add(changeData);

        List<CdcModel> changesResultList = new ArrayList<>();
        CdcModel cdcModel = new CdcModel();
        cdcModel.setCreationDate(LocalDateTime.parse("2018-12-31 23:59:59.993", DATE_TIME_FORMATTER));
        cdcModel.setOperation(String.valueOf(operationNumber[0]));
        cdcModel.setSchema("dbo");
        cdcModel.setTableName("tableNameTest");
        HashMap<String, String> tableChanges = new HashMap<>();
        tableChanges.put("testColumn1", "column 1 value");
        tableChanges.put("testColumn2", "column 2 value");
        tableChanges.put("testColumn3", "column 3 value");
        changesResultList.add(cdcModel);

        LastRunDate lastRunDate = new LastRunDate();
        lastRunDate.setEndDate(LocalDateTime.now());
        lastRunDate.setTableName(changeData.getCaptureInstance());

        when(changeDataRepository.getChangeData()).thenReturn(changeDataList);
        when(changeDataRepository.getChangesCountAfterLastRunDate(changeData, operationNumber, lastRunDate.getEndDate())).thenThrow(new IllegalArgumentException("Testing Exception Handling"));
        int offset = cdcCtrl.calculateOffset(pageSize, 1);
        when(changeDataRepository.getChangesAfterLastRunDate(changeData, offset, lastRunDate.getEndDate(), operationNumber, pageSize)).thenReturn(changesResultList);
        when(lastRunDateRepository.save(any())).thenReturn(any());
        when(lastRunDateRepository.findTopOneByTableNameOrderByEndDate("tableNameTest")).thenReturn(lastRunDate);
        cdcService.getChangesWithPaginationAfterLastRunDate(pageSize, operationNumber, sourceCompany);
        Mockito.verify(lastRunDateRepository).save(Mockito.argThat(cdcProcessObject -> cdcProcessObject.getEndDate() != null));
    }

    @Test
    public void testGetChangesWithPaginationAfterLastRunDateForFirstCdcProcess() {
        System.out.println("getChangesWithPaginationAfterLastRunDate");
        LocalDateTime testdate = LocalDateTime.parse("1900-12-15 23:59:48.000", DATE_TIME_FORMATTER);
        int pageSize = 10;
        int[] operationNumber = {0};
        int sourceCompany = 0;
        int listCount = 5;

        Collection<ChangeData> changeDataList = new ArrayList<>();
        ChangeData changeData = new ChangeData();
        changeData.setCaptureInstance("tableNameTest");
        changeData.setColumnNames("testColumn1,testColumn2,testColumn3");
        changeDataList.add(changeData);

        List<Object[]> changesResultList = new ArrayList<>();
        Object[] changes = {"2018-12-31 23:59:59.999", "column 1 value", "column 2 value", "column 3 value"};
        changesResultList.add(changes);

        LastRunDate lastRunDate = new LastRunDate();
        lastRunDate.setTableName(changeData.getCaptureInstance());

        when(changeDataRepository.getChangeData()).thenReturn(changeDataList);
        when(lastRunDateRepository.save(any())).thenReturn(any());
        when(lastRunDateRepository.findTopOneByTableNameOrderByEndDate("tableNameTest")).thenReturn(lastRunDate);
        cdcService.getChangesWithPaginationAfterLastRunDate(pageSize, operationNumber, sourceCompany);
        Mockito.verify(changeDataRepository).getChangesCountAfterLastRunDate(any(ChangeData.class), any(int[].class), Mockito.argThat(endDate -> endDate != null));
    }

    /**
     * Test of getAllSendingData method, of class CdcBoundary.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetAllSendingDataForPageSizeZero() {
        System.out.println("getAllSendingData");
        int pageSize = 0;
        cdcService.getAllSendingData(pageSize);
    }

    @Test
    public void testGetAllSendingDataForExceptionHandling() {
        System.out.println("getAllSendingData");
        int pageSize = 1;
        List<CdcJsonObject> cdcJSONObjects = new ArrayList<>();
        CdcJsonObject changeDataJSONObject = new CdcJsonObject();
        changeDataJSONObject.setJsonObject("{key:value}");
        cdcJSONObjects.add(changeDataJSONObject);
        when(cdcJsonObjectRepository.findWhereSuccessfulSendDateIsNULL(PageRequest.of(0, pageSize))).thenReturn(cdcJSONObjects);
        cdcService.getAllSendingData(pageSize);
    }

    @Test
    public void testGetAllSendingDataForValidInput() {
        System.out.println("getAllSendingData");
        int pageSize = 1;
        List<CdcJsonObject> cdcJSONObjects = new ArrayList<>();
        CdcJsonObject changeDataJSONObject = new CdcJsonObject();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("testField", "testValue");
        changeDataJSONObject.setJsonObject(objectNode.toString());
        changeDataJSONObject.setId(0);
        cdcJSONObjects.add(changeDataJSONObject);
        when(cdcJsonObjectRepository.findWhereSuccessfulSendDateIsNULL(new PageRequest(0, pageSize))).thenReturn(cdcJSONObjects);

        Collection<ObjectNode> result = cdcService.getAllSendingData(pageSize);
        assertThat(1, Is.is(result.size()));
    }

    /**
     * Test of logSentData method, of class CdcBoundary.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLogSentDataForEmptySentData() {
        System.out.println("logSentData");
        cdcService.logSentData(null, false);
    }

    @Test
    public void testLogSentDataForSendingDataFailure() {
        System.out.println("logSentData");
        List<ObjectNode> jsonObjects = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("jsonObjectID", "0");
        jsonObjects.add(objectNode);
        boolean success = false;
        cdcService.logSentData(jsonObjects, success);
        Mockito.verifyZeroInteractions(cdcJsonObjectRepository);
    }

    @Test
    public void testLogSentDataForSendingDataSuccess() {
        System.out.println("logSentData");
        List<ObjectNode> jsonObjects = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("jsonObjectID", "0");
        jsonObjects.add(objectNode);
        boolean success = true;
        cdcService.logSentData(jsonObjects, success);
        Mockito.verify(cdcJsonObjectRepository).updateSentJsonObjects(any(LocalDateTime.class), any(List.class));
    }
}
