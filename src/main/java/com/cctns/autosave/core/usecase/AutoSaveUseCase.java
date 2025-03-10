package com.cctns.autosave.core.usecase;

import com.cctns.autosave.core.domain.AutoSaveDomain;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface AutoSaveUseCase {

    Object submitAutoSaveData(AutoSaveDomain autoSaveData) throws JsonProcessingException;

    Object getAutoSaveData(String key);
}
