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
public class AutosaveDraftListRequestDto extends CommonParamsDTO{

    @NotNull(message = "moduleName is mandatory")
    @JsonProperty("module")
    private String moduleName;

    @NotNull(message = "Page number and Page size is mandatory")
   private Pageable pageable;

    private Long firRegNum; //Added for final Form
}
