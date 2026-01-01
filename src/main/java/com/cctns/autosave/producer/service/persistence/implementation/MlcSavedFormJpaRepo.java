package com.cctns.autosave.producer.service.persistence.implementation;

import com.cctns.autosave.producer.service.persistence.entity.MlcSavedFormEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MlcSavedFormJpaRepo extends JpaRepository<MlcSavedFormEntity, Long> {

    @Transactional
    @Modifying
    @Query("""
            update MlcSavedFormEntity m set m.injuredName = ?1, m.mlcType = ?2, m.mlcSubType = ?3
            where m.mlcSavedNum = ?4 AND (m.injuredName IS NULL OR m.injuredName <> ?1) 
            AND (m.mlcType IS NULL OR m.mlcType <> ?2) 
            AND ( m.mlcSubType IS NULL OR m.mlcSubType <> ?3) """)
    int updateInjuredNameAndMlcTypeAndMlcSubTypeByMlcSavedNum(String injuredName, String mlcType, String mlcSubType, Long mlcSavedNum);
}
