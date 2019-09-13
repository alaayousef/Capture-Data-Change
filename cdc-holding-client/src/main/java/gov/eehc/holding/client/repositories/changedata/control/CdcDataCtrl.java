package gov.eehc.holding.client.repositories.changedata.control;

import gov.eehc.holding.client.repositories.changedata.entity.CdcModel;
import gov.eehc.holding.client.repositories.changedata.entity.ChangeData;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static gov.eehc.holding.client.common.Constants.DATE_TIME_FORMATTER;

/**
 * @author aabdelrahman
 */
@Service
public class CdcDataCtrl {

    public List<CdcModel> getCdcModels(List<Object[]> cdcChanges, ChangeData changeData) {
        String[] columnNames = changeData.getColumnNames().split(",");
        List<CdcModel> cdcModelList = new ArrayList<>();
        cdcChanges.forEach(cdcChange -> {
            CdcModel cdcModel = new CdcModel();
            cdcModel.setOperation(String.valueOf(cdcChange[0]));
            String creationDateString = String.valueOf(cdcChange[1]);
            if (creationDateString.length() == 22) {
                creationDateString += "0";
            }
            LocalDateTime creationDate = LocalDateTime.parse(creationDateString, DATE_TIME_FORMATTER);
            cdcModel.setCreationDate(creationDate);
            cdcModel.setSchema(changeData.getCaptureInstance().substring(0, 3));
            cdcModel.setTableName(changeData.getCaptureInstance().substring(4));
            HashMap<String, String> tableChanges = new HashMap<>();
            for (int i = 0; i < columnNames.length; i++) {
                tableChanges.put(columnNames[i], String.valueOf(cdcChange[i + 2]));
            }
            cdcModel.setTableChanges(tableChanges);
            cdcModelList.add(cdcModel);
        });
        return cdcModelList;
    }

    public String getOperations(int[] operationNumbers) {
        return Arrays.stream(operationNumbers).
                mapToObj(String::valueOf).
                collect(Collectors.joining(","));
    }
}
