package com.cctns.autosave.persistence.implementation;

import com.cctns.autosave.persistence.entities.GeneralDiarySaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface GdSavedFormRepo extends JpaRepository<GeneralDiarySaveEntity,Long> {
    boolean existsByGdSaveNum(@NonNull String gdSaveNum);
}
