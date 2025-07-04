package com.cctns.autosave.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutoSaveRequestDto {

    @NotNull(message = "moduleName field is mandatory")
    private String moduleName;
    @NotNull(message = "The savedNum is mandatory")
    private String savedNum;
    @NotNull(message = "jsonData field is mandatory")
    private LinkedHashMap<String, Object> jsonData;
    @NotNull(message = "complainantDraftName is mandatory")
    private String complainantDraftName;

    //For mlc :
    private String mlcType;
    private String mlcSubType;
}
