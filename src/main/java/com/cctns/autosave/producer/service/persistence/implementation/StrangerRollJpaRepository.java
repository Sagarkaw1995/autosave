package com.cctns.autosave.producer.service.persistence.implementation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.cctns.autosave.producer.service.persistence.entity.TStrangerSavedEntity;

public interface StrangerRollJpaRepository extends JpaRepository<TStrangerSavedEntity,Long> {

    @Transactional
    @Modifying
    @Query("update TStrangerSavedEntity u set u.informantName = ?1 where u.strngrSavedNum = ?2  AND (u.informantName IS NULL OR u.informantName <> ?1) ")
    int updateInformantNameBySavedNum(String informantName, Long savedNum);
}
