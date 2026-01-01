package com.cctns.autosave.producer.service.core.external.port;

import com.cctns.autosave.producer.service.core.domain.AutosaveDomain;

public interface MicroserviceComms {

    public AutosaveDomain fetchJsonDataFromS3(AutosaveDomain request);
}
