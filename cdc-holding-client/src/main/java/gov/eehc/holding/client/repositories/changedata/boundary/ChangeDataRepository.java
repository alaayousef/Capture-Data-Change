package gov.eehc.holding.client.repositories.changedata.boundary;

import gov.eehc.holding.client.repositories.changedata.control.CdcDataCtrl;
import gov.eehc.holding.client.repositories.changedata.entity.CdcModel;
import gov.eehc.holding.client.repositories.changedata.entity.ChangeData;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static gov.eehc.holding.client.common.Constants.DATE_TIME_FORMATTER;

/**
 * @author Mawaziny
 */
@Repository
public class ChangeDataRepository {

    @PersistenceContext
    private EntityManager em;

    private final CdcDataCtrl ctrl;

    @Autowired
    public ChangeDataRepository(CdcDataCtrl ctrl) {
        this.ctrl = ctrl;
    }

    @SuppressWarnings("unchecked")
    public Collection<ChangeData> getChangeData() {
        String sql = "SELECT ct.object_id as objectId, ct.capture_instance as captureInstance,"
                + " STUFF((SELECT ',' + T.column_name"
                + "        FROM cdc.captured_columns T"
                + "        WHERE ct.object_id = T.object_id FOR XML PATH('')), 1, 1, '') as columnNames "
                + " FROM cdc.change_tables ct"
                + " JOIN cdc.captured_columns cc ON ct.object_id = cc.object_id"
                + " GROUP BY ct.object_id, ct.capture_instance";
        return em.createNativeQuery(sql).
                unwrap(org.hibernate.query.Query.class).
                setResultTransformer(Transformers.aliasToBean(ChangeData.class)).
                list();
    }

    public Long getChangesCountAfterLastRunDate(ChangeData changeData, int[] operationNumbers, LocalDateTime lastRunDate) {
        String operations = ctrl.getOperations(operationNumbers);
        String sql = "SELECT count(*) " +
                " FROM cdc." + changeData.getCaptureInstance() + "_CT" +
                " WHERE __$operation IN (" + operations + ")" +
                "   AND sys.fn_cdc_map_lsn_to_time(__$start_lsn) > CONVERT(DateTime, '" + lastRunDate.format(DATE_TIME_FORMATTER) + "')";
        Integer listCount = (int) em.createNativeQuery(sql).getSingleResult();
        return listCount.longValue();
    }

    @SuppressWarnings("unchecked")
    public List<CdcModel> getChangesAfterLastRunDate(ChangeData changeData, int offset, LocalDateTime lastRunDate, int[] operationNumbers, int pageSize) {
        String operations = ctrl.getOperations(operationNumbers);
        String sql = "SELECT __$operation, sys.fn_cdc_map_lsn_to_time(__$start_lsn)," + changeData.getColumnNames() +
                " FROM cdc." + changeData.getCaptureInstance() + "_CT" +
                " WHERE __$operation IN (" + operations + ")" +
                "     AND sys.fn_cdc_map_lsn_to_time(__$start_lsn) > CONVERT(DateTime, '" + lastRunDate.format(DATE_TIME_FORMATTER) + "')" +
                " ORDER BY __$start_lsn" +
                " OFFSET " + offset + " ROWS" +
                " FETCH NEXT " + pageSize + " ROWS ONLY";
        List<Object[]> resultListPaginated = em.createNativeQuery(sql).getResultList();
        return ctrl.getCdcModels(resultListPaginated, changeData);
    }
}
