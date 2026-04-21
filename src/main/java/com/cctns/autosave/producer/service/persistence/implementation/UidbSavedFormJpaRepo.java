package com.cctns.autosave.producer.service.persistence.implementation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.cctns.autosave.producer.service.persistence.entity.UidbSavedFormEntity;

public interface UidbSavedFormJpaRepo extends JpaRepository<UidbSavedFormEntity,Long> {
    @Transactional
    @Modifying
    @Query("update UidbSavedFormEntity u set u.informantName = ?1 where u.uidbSavedNum = ?2  AND (u.informantName IS NULL OR u.informantName <> ?1)")
    int updateInformantNameByUidbSavedNum(String informantName, Long uidbSavedNum);
}
