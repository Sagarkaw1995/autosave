package com.cctns.autosave.producer.service.web.controller;

import com.cctns.autosave.producer.service.constants.Constants;
import com.cctns.autosave.producer.service.core.domain.AutosaveDomain;
import com.cctns.autosave.producer.service.core.domain.PageDomain;
import com.cctns.autosave.producer.service.core.usecase.AutosaveUseCase;
import com.cctns.autosave.producer.service.mapper.WebDomainMapper;
import com.cctns.autosave.producer.service.web.dto.request.AutosaveDraftListRequestDto;
import com.cctns.autosave.producer.service.web.dto.request.AutosaveGetRequestDto;
import com.cctns.autosave.producer.service.web.dto.request.AutosaveRequestDto;
import com.cctns.autosave.producer.service.web.dto.request.ModuleRequest;
import com.cctns.autosave.producer.service.web.dto.response.ApiResponse;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveCreateResponse;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveDeleteResponse;
import com.cctns.autosave.producer.service.web.dto.response.GetFormDataResponse;
import com.cctns.autosave.producer.service.web.dto.response.UpdateResponseDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Copyright: NCRB
 * Project Name: CCTNS 2.0
 * Class Name: AutosaveProducerController.java
 * Description: Rest controller for Auto-save
 * @version v1.0
 * @since 2025-08-04
 */
@Slf4j
@RestController
@RequestMapping("/common/auto-save/")
public class AutosaveProducerController {

private final ModelMapper modelMapper;
private final AutosaveUseCase autosaveUseCase;
private final WebDomainMapper webDomainMapper;

    public AutosaveProducerController(ModelMapper modelMapper, AutosaveUseCase autosaveUseCase, WebDomainMapper webDomainMapper) {
        this.modelMapper = modelMapper;
        this.autosaveUseCase = autosaveUseCase;
        this.webDomainMapper = webDomainMapper;
    }

    /**
     * Creates a new draft for auto-save
     *
     * @param request Module request {@link ModuleRequest}
     * @return Response entity for draft creation data  {@link ResponseEntity<ApiResponse<AutosaveCreateResponse>>}
     */
    @PostMapping("create-draft")
    public ResponseEntity<ApiResponse<AutosaveCreateResponse>> createAutoSaveDraft(@Valid @RequestBody ModuleRequest request) {
        AutosaveCreateResponse response = autosaveUseCase.createDraft(webDomainMapper.mapsModuleRequestToAutosaveDomain(request));
        ApiResponse<AutosaveCreateResponse> apiResponse = ApiResponse.<AutosaveCreateResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .message(Constants.DRAFT_CREATED_SUCCESSFULLY)
                .data(response)
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

    /**
     * Persists The JSON In Redis
     *
     * @param request (Autosave Request)
     * @return ResponseEntity
     */
    @PostMapping("save-draft-data")
    public ResponseEntity<?> submitAutoSave(@Valid @RequestBody AutosaveRequestDto request) {
        UpdateResponseDto response = autosaveUseCase.persistAutosaveData(modelMapper.map(request, AutosaveDomain.class));
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .message(Constants.SAVE_DRAFT_SUCCESSFULLY)
                .data(response)
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

    /**
     * Fetches Draft Data From Redis
     *
     * @param request
     * @return
     */
    @PostMapping("get-draft-data")
    public ResponseEntity<?> getAutoSaveData(@Valid @RequestBody AutosaveGetRequestDto request) {
        GetFormDataResponse response = autosaveUseCase.fetchAutosaveData(modelMapper.map(request, AutosaveDomain.class));
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .message(Constants.FETCH_DRAFT_SUCCESSFULLY)
                .data(response)
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

    /**
     * Fetches Autosave Draft List
     * @param request
     * @return
     */
    @PostMapping("get-draft-list")
    public ResponseEntity<?> fetchAutosaveDraftList(@Valid @RequestBody AutosaveDraftListRequestDto request) {
        PageDomain<List<LinkedHashMap<String, Object>>> response = autosaveUseCase.fetchAutosaveDraftList(modelMapper.map(request, AutosaveDomain.class));
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .message(Constants.FETCH_DRAFT_SUCCESSFULLY)
                .data(response)
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

    /**
     * Deletes autosave draft from draft list
     * @param request
     * @return
     */
    @PostMapping("delete-draft")
    public ResponseEntity<?> deleteAutosaveDraft(@Valid @RequestBody AutosaveGetRequestDto request) {
        AutosaveDeleteResponse response = autosaveUseCase.deleteAutosaveDraftList(modelMapper.map(request, AutosaveDomain.class));
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .message(Constants.FETCH_DRAFT_SUCCESSFULLY)
                .data(response)
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }
}
