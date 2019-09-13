package gov.eehc.holding.client.business.cdc.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.eehc.holding.client.repositories.cdcjsonobject.entity.CdcJsonObject;
import gov.eehc.holding.client.repositories.changedata.entity.CdcModel;
import gov.eehc.holding.client.repositories.lastrun.entity.LastRunDate;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static gov.eehc.holding.client.common.Constants.DATE_TIME_FORMATTER;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author aabdelrahman
 */
public class CdcCtrlTest {

    private CdcCtrl cdcCtrl;

    @Before
    public void setUp() {
        cdcCtrl = new CdcCtrl();
    }

    /**
     * Test of composeJsonObject method, of class CdcController.
     */
    @Test
    public void testComposeJSONObjectForChangesObjectIsNull() {
        System.out.println("composeJsonObject");
        CdcModel changes = null;
        int sourceCompany = 0;
        int expResult = 0;
        ObjectNode result = cdcCtrl.composeJsonObject(changes, sourceCompany);
        assertThat(expResult, is(result.size()));
    }

    @Test
    public void testComposeJSONObjectForOperationNumberIsSet() {
        System.out.println("composeJsonObject");
        int operationNumber = 1;
        int sourceCompany = 0;
        CdcModel cdcModel = new CdcModel();
        cdcModel.setOperation(String.valueOf(operationNumber));
        cdcModel.setTableName("tableName");
        cdcModel.setSchema("dbo");
        cdcModel.setCreationDate(LocalDateTime.now());
        HashMap<String, String> tableChanges = new HashMap<>();
        tableChanges.put("column1", "changes for column1");
        tableChanges.put("column2", "changes for column2");
        tableChanges.put("column3", "changes for column3");
        cdcModel.setTableChanges(tableChanges);
        ObjectNode result = cdcCtrl.composeJsonObject(cdcModel, sourceCompany);
        assertThat(operationNumber, is(result.get("operation").asInt()));
    }

    @Test
    public void testComposeJSONObjectForSourceCompanyIsSet() {
        System.out.println("composeJsonObject");
        int operationNumber = 1;
        CdcModel cdcModel = new CdcModel();
        cdcModel.setOperation(String.valueOf(operationNumber));
        cdcModel.setTableName("tableName");
        cdcModel.setSchema("dbo");
        cdcModel.setCreationDate(LocalDateTime.now());
        HashMap<String, String> tableChanges = new HashMap<>();
        tableChanges.put("column1", "changes for column1");
        tableChanges.put("column2", "changes for column2");
        tableChanges.put("column3", "changes for column3");
        cdcModel.setTableChanges(tableChanges);
        int sourceCompany = 0;
        ObjectNode result = cdcCtrl.composeJsonObject(cdcModel, sourceCompany);
        assertThat(sourceCompany, is(result.get("sourceCompany").asInt()));
    }

    @Test
    public void testComposeJSONObjectForTableNameIsSet() {
        System.out.println("composeJsonObject");
        int operationNumber = 1;
        CdcModel cdcModel = new CdcModel();
        cdcModel.setOperation(String.valueOf(operationNumber));
        cdcModel.setTableName("tableName");
        cdcModel.setSchema("dbo");
        cdcModel.setCreationDate(LocalDateTime.now());
        HashMap<String, String> tableChanges = new HashMap<>();
        tableChanges.put("column1", "changes for column1");
        tableChanges.put("column2", "changes for column2");
        tableChanges.put("column3", "changes for column3");
        cdcModel.setTableChanges(tableChanges);
        int sourceCompany = 0;
        ObjectNode result = cdcCtrl.composeJsonObject(cdcModel, sourceCompany);
        assertThat(cdcModel.getTableName(), is(result.get("tableName").asText()));
    }

    @Test
    public void testComposeJSONObjectForSchemaIsSet() {
        System.out.println("composeJsonObject");
        int operationNumber = 1;
        CdcModel cdcModel = new CdcModel();
        cdcModel.setOperation(String.valueOf(operationNumber));
        cdcModel.setTableName("tableName");
        cdcModel.setSchema("dbo");
        cdcModel.setCreationDate(LocalDateTime.now());
        HashMap<String, String> tableChanges = new HashMap<>();
        tableChanges.put("column1", "changes for column1");
        tableChanges.put("column2", "changes for column2");
        tableChanges.put("column3", "changes for column3");
        cdcModel.setTableChanges(tableChanges);
        int sourceCompany = 0;
        ObjectNode result = cdcCtrl.composeJsonObject(cdcModel, sourceCompany);
        assertThat(cdcModel.getSchema(), is(result.get("schema").asText()));
    }

    @Test
    public void testComposeJSONObjectForSizeOfOutputShouldBeSizeOfColumnNamesPlusFour() {
        //One for Source Company, one for operation number, one for table name and one for the schema name
        System.out.println("composeJsonObject");
        int operationNumber = 1;
        CdcModel cdcModel = new CdcModel();
        cdcModel.setOperation(String.valueOf(operationNumber));
        cdcModel.setTableName("tableName");
        cdcModel.setSchema("dbo");
        cdcModel.setCreationDate(LocalDateTime.now());
        HashMap<String, String> tableChanges = new HashMap<>();
        tableChanges.put("column1", "changes for column1");
        tableChanges.put("column2", "changes for column2");
        tableChanges.put("column3", "changes for column3");
        cdcModel.setTableChanges(tableChanges);
        int sourceCompany = 0;
        ObjectNode result = cdcCtrl.composeJsonObject(cdcModel, sourceCompany);
        assertThat(result.size(), is(tableChanges.size() + 5));
    }

    @Test
    public void testComposeJSONObjectForColumnValuesAreSetCorrectly() {
        System.out.println("composeJsonObject");
        int operationNumber = 1;
        CdcModel cdcModel = new CdcModel();
        cdcModel.setOperation(String.valueOf(operationNumber));
        cdcModel.setTableName("tableName");
        cdcModel.setSchema("dbo");
        cdcModel.setCreationDate(LocalDateTime.now());
        HashMap<String, String> tableChanges = new HashMap<>();
        tableChanges.put("column1", "changes for column1");
        tableChanges.put("column2", "changes for column2");
        tableChanges.put("column3", "changes for column3");

        cdcModel.setTableChanges(tableChanges);
        int sourceCompany = 0;
        ObjectNode result = cdcCtrl.composeJsonObject(cdcModel, sourceCompany);
        assertThat(tableChanges.get("column1"), is(result.get("column1").asText()));
        assertThat(tableChanges.get("column2"), is(result.get("column2").asText()));
        assertThat(tableChanges.get("column3"), is(result.get("column3").asText()));
    }

    /**
     * Test of calculateNumberOfPages method, of class CdcController.
     */
    @Test
    public void testCalculateNumberOfPages() {
        System.out.println("calculateNumberOfPages");
        long listCount = 100;
        int pageSize = 50;
        long value = listCount / pageSize;
        long expResult = value;
        long result = cdcCtrl.calculateNumberOfPages(listCount, pageSize);
        assertThat(expResult, is(result));
        pageSize = 40;
        expResult = (int) Math.ceil(value) + 1;
        result = cdcCtrl.calculateNumberOfPages(listCount, pageSize);
        assertThat(expResult, is(result));

    }

    /**
     * Test of calculateOffset method, of class CdcController.
     */
    @Test
    public void testCalculateOffset() {
        System.out.println("calculateOffset");
        int pageSize = 50;
        int pageNumber = 1;
        int expResult = pageNumber * (pageNumber - 1);
        int result = cdcCtrl.calculateOffset(pageSize, pageNumber);
        assertThat(expResult, is(result));
    }

    /**
     * Test of prepareDataBeforeSending method, of class CdcController.
     */
    @Test
    public void testPrepareDataBeforeSendingForEmptyOrNullSendingDataShouldReturnEmptyList() throws IOException {
        System.out.println("prepareDataBeforeSending");
        List<CdcJsonObject> sendingData = null;
        int expResult = 0;
        List<ObjectNode> result;
        result = (List<ObjectNode>) cdcCtrl.prepareDataBeforeSending(sendingData);
        assertThat(expResult, is(result.size()));
    }

    @Test
    public void testPrepareDataBeforeSendingForJsonObjectID() throws IOException {
        System.out.println("prepareDataBeforeSending");
        CdcJsonObject changeDataJSONObject = new CdcJsonObject();
        changeDataJSONObject.setId(0);
        List<CdcJsonObject> sendingData = new ArrayList<>();
        sendingData.add(changeDataJSONObject);
        List<ObjectNode> result;
        result = (List<ObjectNode>) cdcCtrl.prepareDataBeforeSending(sendingData);
        assertThat(changeDataJSONObject.getId(), is(result.get(0).get("jsonObjectID").asLong()));

    }

    @Test
    public void testPrepareDataBeforeSendingForObjectThatHasValidJsonData() throws IOException {
        System.out.println("prepareDataBeforeSending");
        CdcJsonObject changeDataJSONObject = new CdcJsonObject();
        changeDataJSONObject.setId(0);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("testField", "testValue");
        changeDataJSONObject.setJsonObject(objectNode.toString());
        List<CdcJsonObject> sendingData = new ArrayList<>();
        sendingData.add(changeDataJSONObject);
        List<ObjectNode> result;
        result = (List<ObjectNode>) cdcCtrl.prepareDataBeforeSending(sendingData);
        assertThat(objectNode.get("testField").asText(), is(result.get(0).get("testField").asText()));

    }

    @Test(expected = IOException.class)
    public void testPrepareDataBeforeSendingForObjectThatHasInvalidJsonData() throws IOException {
        System.out.println("prepareDataBeforeSending");
        CdcJsonObject changeDataJSONObject = new CdcJsonObject();
        changeDataJSONObject.setId(0);
        changeDataJSONObject.setJsonObject("{wrongStructure:test}");
        List<CdcJsonObject> sendingData = new ArrayList<>();
        sendingData.add(changeDataJSONObject);
        List<ObjectNode> result;
        result = (List<ObjectNode>) cdcCtrl.prepareDataBeforeSending(sendingData);
        assertThat(0, is(result.size()));
    }

    /**
     * Test of extractJsonObjectIds method, of class CdcController.
     */
    @Test
    public void testExtractJsonObjectIDsForEmptyOrNullInputShouldReturnEmptyList() {
        List<ObjectNode> jsonObjects = new ArrayList<>();
        Collection<Long> cdcJsonObjects = cdcCtrl.extractJsonObjectIds(jsonObjects);
        assertThat(0, is(cdcJsonObjects.size()));
        cdcJsonObjects = cdcCtrl.extractJsonObjectIds(null);
        assertThat(0, is(cdcJsonObjects.size()));
    }

    @Test
    public void testExtractJsonObjectIDsForValidInput() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("jsonObjectID", "0");
        List<ObjectNode> jsonObjects = new ArrayList<>();
        jsonObjects.add(objectNode);
        List<Long> cdcJsonObjects = (List<Long>) cdcCtrl.extractJsonObjectIds(jsonObjects);
        assertThat(objectNode.get("jsonObjectID").asLong(), is(cdcJsonObjects.get(0)));
    }

    /**
     * Test of createNewChangeDataJsonObject method, of class CdcController.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateNewChangeDataJSONObjectForNullInputShouldReturnNullOutput() {
        cdcCtrl.createNewChangeDataJsonObject(null);
    }

    @Test
    public void testCreateNewChangeDataJSONObjectForValidInput() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("jsonObjectID", "0");
        CdcJsonObject changeDataJSONObject = cdcCtrl.createNewChangeDataJsonObject(objectNode);
        assertThat(objectNode.toString(), is(changeDataJSONObject.getJsonObject()));
    }

    /**
     * Test of getPreviousCdcRunDate method, of class CdcController.
     */
    @Test
    public void testGetPreviousCdcRunDateWhenPreviousCdcProcessIsNull() {
        LastRunDate lastRunDate = null;
        LocalDateTime expectedDate = LocalDateTime.parse("1900-12-29 23:59:48.000", DATE_TIME_FORMATTER);
        LocalDateTime resultDate = cdcCtrl.getPreviousCdcRunDate(lastRunDate);
        assertThat(expectedDate.compareTo(resultDate), is(0));
    }

    @Test
    public void testGetPreviousCdcRunDateWhenPreviousCdcProcessIsNotNull() {
        LastRunDate lastRunDate = new LastRunDate();
        lastRunDate.setEndDate(LocalDateTime.now());
        LocalDateTime expectedDate = lastRunDate.getEndDate();
        LocalDateTime resultDate = cdcCtrl.getPreviousCdcRunDate(lastRunDate);
        assertThat(expectedDate.compareTo(resultDate), is(0));
    }
}
