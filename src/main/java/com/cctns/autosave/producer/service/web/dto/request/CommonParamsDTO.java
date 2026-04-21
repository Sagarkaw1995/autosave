package com.cctns.autosave.producer.service.web.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonParamsDTO {
    //user credentials
    @NotNull(message = "staffId is mandatory")
    private Long staffId;
    private String loginId;
    @NotNull(message = "langCd is mandatory")
    private Integer langCd;
    private List<Integer> roleCd;   //current selected role
    private Long officeCd;
    private Integer stateCd;
    @NotNull(message = "stateId is mandatory")
    private Long stateId;
    private Integer districtCd;
    @NotNull(message = "districtId is mandatory")
    private Long districtId;
    private Integer psCd;
    private Long psId;
    private List<Integer> psIdList;
    private Integer officeTypeCd;
    private Integer rankCd;
    private Integer officeLevelCd;
    private List<Integer> allowedRoleCd;    //all available role for that user
    @NotNull(message = "oicStaffId is mandatory")
    private Long oicStaffId;
    private String oicLoginId;
}
