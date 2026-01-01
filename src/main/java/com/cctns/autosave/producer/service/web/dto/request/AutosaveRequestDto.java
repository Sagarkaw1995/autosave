package com.cctns.autosave.producer.service.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutosaveRequestDto {

    private Integer langCd;
    private List<Integer> roleCd;
    private String loginId;
    private Long officeCd;
    private Integer stateCd;
    private String stateName;
    private Integer districtCd;
    private String districtName;
    private Integer psCd;
    private String psName;
    private Integer officeTypeCd;
    private Integer rankCd;
    private String rankName;
    private Integer officeLevelCd;
    private List<Integer> allowedRoleCd;

    @NotNull(message = "moduleName field is mandatory")
    private String moduleName;
    @NotNull(message = "The savedNum is mandatory")
    private Long savedNum;
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
