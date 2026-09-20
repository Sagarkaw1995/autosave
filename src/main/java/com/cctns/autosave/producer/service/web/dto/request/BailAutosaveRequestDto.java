package com.cctns.autosave.producer.service.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BailAutosaveRequestDto extends ModuleRequest {
    @NotNull(message = "Arrest Serial Number Cannot Be Null For Bail Autosave")
    private Long arrSurrSrNo;
    private String accusedName;
}
