package com.cctns.autosave.producer.service.external.adapter;


import com.cctns.autosave.producer.service.core.domain.AutosaveDomain;
import com.cctns.autosave.producer.service.web.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${autosave-consumer-ms-name}", url = "${autosave-consumer-ms-url}")
public interface AutosaveConsumerClient {

    @PostMapping("${autosave-consumer-get-url}")
    ApiResponse<AutosaveDomain> getAutosaveData(@RequestBody AutosaveDomain request);
}
