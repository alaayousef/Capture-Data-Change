package gov.eehc.holding.client.business.cdc.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.eehc.holding.client.repositories.cdcjsonobject.entity.CdcJsonObject;
import gov.eehc.holding.client.repositories.changedata.entity.CdcModel;
import gov.eehc.holding.client.repositories.lastrun.entity.LastRunDate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static gov.eehc.holding.client.common.Constants.DATE_TIME_FORMATTER;

/**
 * @author aabdelrahman
 */
@Service
public class CdcCtrl {

    public ObjectNode composeJsonObject(CdcModel model, int sourceCompany) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        if (model == null || model.getTableChanges().isEmpty()) {
            return objectNode;
        }
        objectNode.put("sourceCompany", sourceCompany);
        objectNode.put("operation", model.getOperation());
        objectNode.put("tableName", model.getTableName());
        objectNode.put("cdcDateTime", model.getCreationDate().format(DATE_TIME_FORMATTER));
        objectNode.put("schema", model.getSchema());
        model.getTableChanges().forEach(objectNode::put);
        return objectNode;
    }

    public Long calculateNumberOfPages(Long listCount, int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page Size Must be greater than 0");
        }
        long value = listCount / pageSize;
        if (listCount % pageSize == 0) {
            return value;
        } else {
            return Double.valueOf(Math.ceil(value) + 1).longValue();
        }
    }

    public int calculateOffset(int pageSize, int pageNumber) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page Size Must be greater than 0");
        }
        return pageSize * (pageNumber - 1);
    }

    public Collection<ObjectNode> prepareDataBeforeSending(List<CdcJsonObject> sendingData) throws IOException {
        Collection<ObjectNode> jsonObjects = new ArrayList<>();
        if (sendingData == null || sendingData.isEmpty()) {
            return jsonObjects;
        }
        ObjectMapper mapper = new ObjectMapper();
        for (CdcJsonObject data : sendingData) {
            ObjectNode jsonNode = mapper.createObjectNode();
            if (data.getJsonObject() != null) {
                jsonNode = (ObjectNode) mapper.readTree(data.getJsonObject());
            }
            jsonNode.put("jsonObjectID", data.getId());
            jsonObjects.add(jsonNode);
        }
        return jsonObjects;
    }

    public Collection<Long> extractJsonObjectIds(Collection<ObjectNode> jsonObjects) {
        if (jsonObjects == null) {
            return new ArrayList<>();
        }
        return jsonObjects.stream().
                map((ObjectNode objectNode) -> objectNode.get("jsonObjectID").asLong()).
                collect(Collectors.toList());
    }

    public CdcJsonObject createNewChangeDataJsonObject(ObjectNode jsonObject) {
        if (jsonObject == null) {
            throw new IllegalArgumentException("Json Object Cannot be null.");
        }
        CdcJsonObject cdcJsonObject = new CdcJsonObject();
        cdcJsonObject.setJsonObject(jsonObject.toString());
        cdcJsonObject.setCreationDate(LocalDateTime.now());
        return cdcJsonObject;
    }

    public LocalDateTime getPreviousCdcRunDate(LastRunDate previousLastRunDate) {
        if (previousLastRunDate == null || previousLastRunDate.getEndDate() == null) {
            return LocalDateTime.parse("1900-12-29 23:59:48.000", DATE_TIME_FORMATTER);
        } else {
            return previousLastRunDate.getEndDate();
        }
    }

    public List<CdcJsonObject> getCdcJsonObjects(List<CdcModel> cdcModels, int sourceCompany) {
        return cdcModels.stream().map(changes -> {
            ObjectNode jsonObject = composeJsonObject(changes, sourceCompany);
            return createNewChangeDataJsonObject(jsonObject);
        }).collect(Collectors.toList());
    }
}
