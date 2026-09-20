package com.cctns.autosave.producer.service.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FirAutosaveRequestDto extends ModuleRequest {
    private Long firRegNum;
    private String initiatedBy;
    private String complainantDraftName;
}
