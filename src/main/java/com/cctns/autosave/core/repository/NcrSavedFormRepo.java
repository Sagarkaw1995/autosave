package com.cctns.autosave.core.repository;

import com.cctns.autosave.persistence.entities.NcrSavedFormEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface NcrSavedFormRepo extends JpaRepository<NcrSavedFormEntity,Long> {
    @Transactional
    @Modifying
    @Query("update NcrSavedFormEntity n set n.complainantName = ?1 where n.ncrSavedNum = ?2  AND (n.complainantName IS NULL OR n.complainantName <> ?1)")
    int updateComplainantNameByNcrSavedNum(String complainantName, Long ncrSavedNum);
}
