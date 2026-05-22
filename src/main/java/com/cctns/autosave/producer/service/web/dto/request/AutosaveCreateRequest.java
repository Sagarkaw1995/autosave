package com.cctns.autosave.producer.service.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutosaveCreateRequest extends CommonParamsDTO {

    @NotNull(message = "moduleName is mandatory")
    @JsonProperty("module")
    private String moduleName;

    private Long firRegNum;
}
