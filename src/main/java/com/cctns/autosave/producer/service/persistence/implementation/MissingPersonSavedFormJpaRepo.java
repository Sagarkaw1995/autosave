package com.cctns.autosave.producer.service.persistence.implementation;

import com.cctns.autosave.producer.service.persistence.entity.MissingPersonSavedFormEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MissingPersonSavedFormJpaRepo extends JpaRepository<MissingPersonSavedFormEntity,Long> {
    @Transactional
    @Modifying
    @Query("update MissingPersonSavedFormEntity m set m.complainantName = ?1 where m.mpersSavedNum = ?2  AND (m.complainantName IS NULL OR m.complainantName <> ?1)")
    int updateComplainantNameByMpersSavedNum(String complainantName, Long mpersSavedNum);
}

