package com.cctns.autosave.web.controller;

import com.cctns.autosave.core.domain.AutoSaveDomain;
import com.cctns.autosave.core.usecase.AutoSaveUseCase;
import com.cctns.autosave.web.dto.request.AutoSaveRequestDto;
import com.cctns.autosave.web.dto.request.JsonDataDto;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common/autoSave/")
public class AutoSaveController {

    private final AutoSaveUseCase autoSaveUseCase;
    private final ModelMapper modelMapper;

    public AutoSaveController(AutoSaveUseCase autoSaveUseCase,ModelMapper modelMapper) {
        this.autoSaveUseCase = autoSaveUseCase;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/saveDraftData")
    public ResponseEntity<?> submitAutoSave(@Valid @RequestBody AutoSaveRequestDto request) {
        return ResponseEntity.ok().body(autoSaveUseCase.submitAutoSaveData(modelMapper.map(request, AutoSaveDomain.class)));
    }

    @PostMapping("/getDraftDataFromRedis")
    public ResponseEntity<?> getAutoSaveData(@Valid @RequestBody JsonDataDto request){
        return ResponseEntity.ok().body(autoSaveUseCase.getAutoSaveData(request.getKey()));
    }
}
