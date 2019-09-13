package gov.eehc.holding.client.repositories.changedata.entity;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author aabdelrahman
 */
public class CdcModel {

    private String tableName;
    private String schema;
    private String operation;
    private LocalDateTime creationDate;
    private HashMap<String, String> tableChanges;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public HashMap<String, String> getTableChanges() {
        return tableChanges;
    }

    public void setTableChanges(HashMap<String, String> tableChanges) {
        this.tableChanges = tableChanges;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

}
