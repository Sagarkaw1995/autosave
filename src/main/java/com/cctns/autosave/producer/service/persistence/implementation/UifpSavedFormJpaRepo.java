package com.cctns.autosave.producer.service.persistence.implementation;

import com.cctns.autosave.producer.service.persistence.entity.UifpSavedFormEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UifpSavedFormJpaRepo extends JpaRepository<UifpSavedFormEntity,Long> {
    @Transactional
    @Modifying
    @Query("update UifpSavedFormEntity u set u.informantName = ?1 where u.uifpSavedNum = ?2  AND (u.informantName IS NULL OR u.informantName <> ?1) ")
    int updateInformantNameBySavedNum(String informantName, Long savedNum);
}
