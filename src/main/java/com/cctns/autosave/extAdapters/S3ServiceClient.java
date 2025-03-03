package com.cctns.autosave.extAdapters;

import com.cctns.autosave.web.dto.request.JsonDataDto;
import com.cctns.autosave.web.dto.response.ApiResponse;
import com.cctns.autosave.web.dto.response.AutoSaveResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${autoSaveS3MsName}",url = "${S3ConsumerMsUrl}")
public interface S3ServiceClient {

    @PostMapping("${getSaveJsonUrl}")
    ResponseEntity<AutoSaveResponseDto> getAutoSaveData(@RequestBody JsonDataDto request);
}
