package gov.eehc.holding.client.repositories.changedata.entity;

import java.io.Serializable;

/**
 * @author Mawaziny
 */
public class ChangeData implements Serializable {

    private static final long serialVersionUID = 3377183417663422233L;

    private long objectId;
    private String captureInstance;
    private String columnNames;

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public String getCaptureInstance() {
        return captureInstance;
    }

    public void setCaptureInstance(String captureInstance) {
        this.captureInstance = captureInstance;
    }

    public String getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public String toString() {
        return "ChangeData{"
                + "objectId='" + objectId + '\''
                + ", captureInstance='" + captureInstance + '\''
                + ", columnNames='" + columnNames + '\''
                + '}';
    }
}
