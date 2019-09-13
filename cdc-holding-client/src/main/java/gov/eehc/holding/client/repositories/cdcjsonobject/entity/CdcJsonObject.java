package gov.eehc.holding.client.repositories.cdcjsonobject.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author aabdelrahman
 */
@Entity
@Table(name = "change_data_json_object")
public class CdcJsonObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "SUCCESSFULL_SEND_DATE")
    private LocalDateTime successfulSendDate;

    @Column(name = "JSON_OBJECT")
    private String jsonObject;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creationDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getSuccessfulSendDate() {
        return successfulSendDate;
    }

    public void setSuccessfulSendDate(LocalDateTime successfulSendDate) {
        this.successfulSendDate = successfulSendDate;
    }

    public String getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CdcJsonObject other = (CdcJsonObject) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

}
