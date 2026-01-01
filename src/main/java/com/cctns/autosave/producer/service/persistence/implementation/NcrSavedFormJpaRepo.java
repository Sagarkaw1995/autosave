package com.cctns.autosave.producer.service.persistence.implementation;

import com.cctns.autosave.producer.service.persistence.entity.NcrSavedFormEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface NcrSavedFormJpaRepo extends JpaRepository<NcrSavedFormEntity,Long> {
    @Transactional
    @Modifying
    @Query("update NcrSavedFormEntity n set n.complainantName = ?1 where n.ncrSavedNum = ?2  AND (n.complainantName IS NULL OR n.complainantName <> ?1)")
    int updateComplainantNameByNcrSavedNum(String complainantName, Long ncrSavedNum);
}
