package com.cctns.autosave.core.repository;

import com.cctns.autosave.persistence.entities.ComplainantSavedFormEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ComplainantSavedFormRepo extends JpaRepository<ComplainantSavedFormEntity,Long> {

    @Transactional
    @Modifying
    @Query("update ComplainantSavedFormEntity c set c.complainantName = ?1 where c.complSavedNum = ?2 AND (c.complainantName IS NULL OR c.complainantName <> ?1)")
    int updateComplainantNameByComplSavedNum(String complainantName, Long complSavedNum);
}
