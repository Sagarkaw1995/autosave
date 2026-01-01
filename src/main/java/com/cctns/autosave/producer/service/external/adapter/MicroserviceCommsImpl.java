package com.cctns.autosave.producer.service.external.adapter;

import com.cctns.autosave.producer.service.core.domain.AutosaveDomain;
import com.cctns.autosave.producer.service.core.external.port.MicroserviceComms;
import com.cctns.autosave.producer.service.web.dto.response.ApiResponse;
import org.springframework.stereotype.Component;

@Component
public class MicroserviceCommsImpl implements MicroserviceComms {

    private final AutosaveConsumerClient autosaveConsumerClient;

    public MicroserviceCommsImpl(AutosaveConsumerClient autosaveConsumerClient) {
        this.autosaveConsumerClient = autosaveConsumerClient;
    }

    @Override
    public AutosaveDomain fetchJsonDataFromS3(AutosaveDomain request) {
        ApiResponse<AutosaveDomain> response = autosaveConsumerClient.getAutosaveData(request);
        return response.getData();
    }
}
