package com.cctns.autosave.producer.service.web.controller;

import com.cctns.autosave.producer.service.constants.Constants;
import com.cctns.autosave.producer.service.core.domain.AutosaveDomain;
import com.cctns.autosave.producer.service.core.domain.PageDomain;
import com.cctns.autosave.producer.service.core.usecase.AutosaveUseCase;
import com.cctns.autosave.producer.service.web.dto.request.AutosaveCreateRequest;
import com.cctns.autosave.producer.service.web.dto.request.AutosaveDraftListRequestDto;
import com.cctns.autosave.producer.service.web.dto.request.AutosaveGetRequestDto;
import com.cctns.autosave.producer.service.web.dto.request.AutosaveRequestDto;
import com.cctns.autosave.producer.service.web.dto.response.ApiResponse;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveCreateResponse;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveDeleteResponse;
import com.cctns.autosave.producer.service.web.dto.response.GetFormDataResponse;
import com.cctns.autosave.producer.service.web.dto.response.UpdateResponseDto;
import jakarta.validation.Valid;
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
 * Controller for the autosave service
 */
@RestController
@RequestMapping("/common/auto-save/")
public class AutosaveProducerController {

private final ModelMapper modelMapper;
private final AutosaveUseCase autosaveUseCase;

    public AutosaveProducerController(ModelMapper modelMapper, AutosaveUseCase autosaveUseCase) {
        this.modelMapper = modelMapper;
        this.autosaveUseCase = autosaveUseCase;
    }

    /**
     * Creates a new draft for autosave
     * @param request
     * @return
     */
    @PostMapping("create-draft")
    public ResponseEntity<?> createAutoSaveDraft(@Valid @RequestBody AutosaveCreateRequest request){
        AutosaveCreateResponse response = autosaveUseCase.sentinelPersist(modelMapper.map(request, AutosaveDomain.class));
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .message(Constants.SAVE_DRAFT_SUCCESSFULLY)
                .data(response)
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

    /**
     * Persists The JSON In Redis
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
     * @param request
     * @return
     */
    @PostMapping("get-draft-data")
    public ResponseEntity<?> getAutoSaveData(@Valid @RequestBody AutosaveGetRequestDto request){
        GetFormDataResponse response =  autosaveUseCase.fetchAutosaveData(modelMapper.map(request, AutosaveDomain.class));
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
