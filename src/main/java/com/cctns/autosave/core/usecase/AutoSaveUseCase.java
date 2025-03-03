package com.cctns.autosave.core.usecase;

import com.cctns.autosave.core.domain.AutoSaveDomain;

public interface AutoSaveUseCase {

    Object submitAutoSaveData(AutoSaveDomain autoSaveData);

    Object getAutoSaveData(String key);
}
