package com.cctns.autosave.producer.service.web.controller;

import com.cctns.autosave.producer.service.constants.Constants;
import com.cctns.autosave.producer.service.core.domain.AutosaveDomain;
import com.cctns.autosave.producer.service.core.usecase.AutosaveUseCase;
import com.cctns.autosave.producer.service.web.dto.request.AutosaveGetRequestDto;
import com.cctns.autosave.producer.service.web.dto.request.AutosaveRequestDto;
import com.cctns.autosave.producer.service.web.dto.response.ApiResponse;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveJsonResponseDto;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveResponseDto;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for the autosave service
 */
@RestController
@RequestMapping("/common/autoSave/")
public class AutosaveProducerController {

private final ModelMapper modelMapper;
private final AutosaveUseCase autosaveUseCase;

    public AutosaveProducerController(ModelMapper modelMapper, AutosaveUseCase autosaveUseCase) {
        this.modelMapper = modelMapper;
        this.autosaveUseCase = autosaveUseCase;
    }

    /**
     * Persists the JSON in Redis
     * @param request (Autosave Request)
     * @return ResponseEntity
     */
    @PostMapping("/saveDraftData")
    public ResponseEntity<?> submitAutoSave(@Valid @RequestBody AutosaveRequestDto request) {
        AutosaveDomain response = autosaveUseCase.sentinelPersist(modelMapper.map(request, AutosaveDomain.class));
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .message(Constants.SAVE_DRAFT_SUCCESSFULLY)
                .data(modelMapper.map(response, AutosaveResponseDto.class))
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/getDraftDataFromRedis")
    public ResponseEntity<?> getAutoSaveData(@Valid @RequestBody AutosaveGetRequestDto request){
    AutosaveDomain response =  autosaveUseCase.fetchAutosaveData(modelMapper.map(request, AutosaveDomain.class));
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .message(Constants.FETCH_DRAFT_SUCCESSFULLY)
                .data(modelMapper.map(response, AutosaveJsonResponseDto.class))
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }
}
