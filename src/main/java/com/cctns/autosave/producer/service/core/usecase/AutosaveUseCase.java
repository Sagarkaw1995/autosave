package com.cctns.autosave.producer.service.core.usecase;

import com.cctns.autosave.producer.service.core.domain.AutosaveDomain;
import com.cctns.autosave.producer.service.core.domain.PageDomain;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveCreateResponse;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveDeleteResponse;
import com.cctns.autosave.producer.service.web.dto.response.GetFormDataResponse;
import com.cctns.autosave.producer.service.web.dto.response.UpdateResponseDto;

import java.util.LinkedHashMap;
import java.util.List;

public interface AutosaveUseCase {

    /**
     * Creates a new autosave draft for the requested module
     *
     * @param request Autosave domain
     * @return Created autosave data
     */
    AutosaveCreateResponse createDraft(AutosaveDomain request);

    /**
     * Persists the key value pair in redis cache
     *
     * @param request (AutosaveDomain)
     * @return AutosaveDomain
     */
    UpdateResponseDto persistAutosaveData(AutosaveDomain request);

    /**
     * Fetches the persisted key value pair from redi / AWS
     *
     * @param request (AutosaveDomain)
     * @return AutosaveDomain
     */
    GetFormDataResponse fetchAutosaveData(AutosaveDomain request);

    /**
     * Fetches autosave draft list from redis with pagination
     *
     * @param request Autosave domain
     */
    PageDomain<List<LinkedHashMap<String, Object>>> fetchAutosaveDraftList(AutosaveDomain request);

    /**
     * Fetches autosave draft list from redis without pagination
     *
     * @param request Autosave domain
     */
    List<LinkedHashMap<String, Object>> fetchAutosaveDraftListWithoutPagination(AutosaveDomain request);

    /**
     * Deletes autosave draft
     *
     * @param request Autosave draft
     * @return
     */
    AutosaveDeleteResponse deleteAutosaveDraftList(AutosaveDomain request);
}
