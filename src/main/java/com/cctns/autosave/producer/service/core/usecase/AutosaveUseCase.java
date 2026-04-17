package com.cctns.autosave.producer.service.core.usecase;

import com.cctns.autosave.producer.service.core.domain.AutosaveDomain;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveCreateResponse;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveDeleteResponse;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveDraftListResponse;
import com.cctns.autosave.producer.service.web.dto.response.GetFormDataResponse;
import com.cctns.autosave.producer.service.web.dto.response.UpdateResponseDto;

public interface AutosaveUseCase {

    /**
     * Creates a new autosave draft
     * @param request Autosave domain
     * @return New created autosave data
     */
    AutosaveCreateResponse sentinelPersist(AutosaveDomain request);

    /**
     * Persists the key value pair in redis cache
     * @param request (AutosaveDomain)
     * @return AutosaveDomain
     */
    UpdateResponseDto persistAutosaveData(AutosaveDomain request);

    /**
     * Fetches the persisted key value pair from redi / AWS
     * @param request (AutosaveDomain)
     * @return AutosaveDomain
     */
    GetFormDataResponse fetchAutosaveData(AutosaveDomain request);

    /**
     * Fetches autosave draft list from redis
     * @param request Autosave domain
     */
  AutosaveDraftListResponse fetchAutosaveDraftList(AutosaveDomain request);

    /**
     * Deletes autosave draft
     * @param request Autosave draft
     * @return
     */
  AutosaveDeleteResponse deleteAutosaveDraftList(AutosaveDomain request);
}
