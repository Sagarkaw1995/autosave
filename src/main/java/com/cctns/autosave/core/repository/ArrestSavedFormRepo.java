package com.cctns.autosave.core.repository;

import com.cctns.autosave.persistence.entities.ArrestSavedFormEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ArrestSavedFormRepo extends JpaRepository<ArrestSavedFormEntity,Long> {

//    @Transactional
//    @Modifying
//    @Query(" update ArrestSavedFormEntity a set a.accusedName = ?1, a.arrestType = ?2 " +
//            " where a.arrestSavedNum = ?3 AND (a.accusedName IS NULL OR a.accusedName <> ?1) " +
//            " AND ( a.arrestType IS NULL OR a.arrestType <> ?2 ) ")
//    int updateAccusedNameAndArrestTypeByArrestSavedNum(String accusedName, String arrestType, Long arrestSavedNum);




    @Transactional
    @Modifying
    @Query(" update ArrestSavedFormEntity a set a.accusedName = ?1 " +
            " where a.arrestSavedNum = ?2 AND (a.accusedName IS NULL OR a.accusedName <> ?1) ")
    int updateAccusedNameByArrestSavedNum(String accusedName, Long arrestSavedNum);
}
