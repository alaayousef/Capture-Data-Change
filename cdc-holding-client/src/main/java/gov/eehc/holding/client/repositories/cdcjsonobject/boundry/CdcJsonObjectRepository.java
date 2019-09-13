package gov.eehc.holding.client.repositories.cdcjsonobject.boundry;

import gov.eehc.holding.client.repositories.cdcjsonobject.entity.CdcJsonObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author aabdelrahman
 */
@Repository
public interface CdcJsonObjectRepository extends JpaRepository<CdcJsonObject, Long> {

    @Query(value = "SELECT c FROM CdcJsonObject c WHERE successfulSendDate IS NULL ORDER BY id")
    List<CdcJsonObject> findWhereSuccessfulSendDateIsNULL(Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "UPDATE CdcJsonObject c SET c.successfulSendDate = :successfulSendDate WHERE c.id IN (:ids)")
    void updateSentJsonObjects(@Param("successfulSendDate") LocalDateTime localDateTime, @Param("ids") List<Long> ids);

    @Transactional
    @Modifying
    @Query(value = "Delete from CdcJsonObject c where successfulSendDate IS NOT NULL")
    void deleteWhereSuccessfulSendDateIsNotNull();
}
