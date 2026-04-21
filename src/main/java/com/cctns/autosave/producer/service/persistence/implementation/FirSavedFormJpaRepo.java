package com.cctns.autosave.producer.service.persistence.implementation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.cctns.autosave.producer.service.persistence.entity.FirSavedFormEntity;

public interface FirSavedFormJpaRepo extends JpaRepository<FirSavedFormEntity,Long> {
    @Transactional
    @Modifying
    @Query("update FirSavedFormEntity f set f.complainantName = ?1 where f.firSavedNum = ?2  AND (f.complainantName IS NULL OR f.complainantName <> ?1)")
    int updateComplainantNameByFirSavedNum(String complainantName, Long firSavedNum);
}
