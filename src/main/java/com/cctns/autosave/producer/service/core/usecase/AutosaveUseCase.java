package com.cctns.autosave.producer.service.core.usecase;

import com.cctns.autosave.producer.service.core.domain.AutosaveDomain;

public interface AutosaveUseCase {

    AutosaveDomain sentinelPersist(AutosaveDomain request);

    /**
     * Persists the key value pair in redis cache
     * @param request (AutosaveDomain)
     * @return AutosaveDomain
     */
    AutosaveDomain persistAutosaveData(AutosaveDomain request);

    /**
     * Fetches the persisted key value pair from redi / AWS
     * @param request (AutosaveDomain)
     * @return AutosaveDomain
     */
    AutosaveDomain fetchAutosaveData(AutosaveDomain request);
}
