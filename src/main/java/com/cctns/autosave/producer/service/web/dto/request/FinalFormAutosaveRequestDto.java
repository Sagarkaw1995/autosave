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
public class FinalFormAutosaveRequestDto extends ModuleRequest {
    @NotNull(message = "Fir Registration Number Cannot Be Null For Final Form Autosave")
    private Long firRegNum;
    private String complainantDraftName;
}
