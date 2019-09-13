package gov.eehc.holding.client.repositories.lastrun.boundry;

import gov.eehc.holding.client.repositories.lastrun.entity.LastRunDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author aabdelrahman
 */
@Repository
public interface LastRunDateRepository extends CrudRepository<LastRunDate, Long> {

    @Query(value = "SELECT TOP 1 * FROM last_run_date WHERE cdc_table_name = :tableName ORDER BY end_date DESC", nativeQuery = true)
    LastRunDate findTopOneByTableNameOrderByEndDate(@Param("tableName") String tableName);

    @Query(value = "SELECT TOP 1 DISTRIBUTION_COMPANY_CODE FROM DISTRIBUTION_COMPANY_STRUCTURE_DIMENSION", nativeQuery = true)
    String getSourceCompany();
}
