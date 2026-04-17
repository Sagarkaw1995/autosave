package com.cctns.autosave.producer.service.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutosaveRequestDto extends CommonParamsDTO{

    @NotNull(message = "moduleName field is mandatory")
    private String moduleName;
    @NotNull(message = "The draftId is mandatory")
    private String draftId;
    @NotNull(message = "jsonData field is mandatory")
    private LinkedHashMap<String, Object> jsonData;
    // @NotNull(message = "complainantDraftName is mandatory")
    private String complainantDraftName;

    //For mlc :
    private String mlcType;
    private String mlcSubType;

    //For Arrest Type :
    // private String arrestType;
}
