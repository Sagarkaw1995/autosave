package com.cctns.autosave.producer.service.persistence.implementation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.cctns.autosave.producer.service.persistence.entity.MlcSavedFormEntity;

public interface MlcSavedFormJpaRepo extends JpaRepository<MlcSavedFormEntity, Long> {

    @Transactional
    @Modifying
    @Query("""
    update MlcSavedFormEntity m
       set m.injuredName = ?1,
           m.mlcType = ?2,
           m.mlcSubType = ?3
     where m.mlcSavedNum = ?4
       and (
            (m.injuredName is null or m.injuredName <> ?1)
         or (m.mlcType is null or m.mlcType <> ?2)
         or (m.mlcSubType is null or m.mlcSubType <> ?3)
       )
""")
    int updateInjuredNameAndMlcTypeAndMlcSubTypeByMlcSavedNum(String injuredName, String mlcType, String mlcSubType, Long mlcSavedNum);
}
