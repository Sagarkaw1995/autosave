package com.cctns.autosave.producer.service.web.dto.request;

import com.cctns.autosave.producer.service.constants.Constants;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonParamsDTO {
    // user credentials
    @NotNull(message = Constants.STAFF_ID_NOT_NULL_MSG)
    private Long staffId;
    private String loginId;
    @NotNull(message =Constants.LANG_CD_NOT_NULL_MSG)
    private Integer langCd;
    @NotNull(message = Constants.OFFICE_CD_NOT_NULL_MSG)
    private Long officeCd;
    @NotNull(message = Constants.STATE_ID_NOT_NULL_MSG)
    private Long stateId;
    private Long districtId;
    private Long psId;
    private Integer officeTypeCd;
    private Integer rankCd;
    private Integer officeLevelCd;
    @NotEmpty(message = Constants.ROLES_NOT_EMPTY_MSG)
    private List<Integer> allowedRoleCd; // all available role for that use
    private Long oicStaffId;
    private String oicLoginId;

    private String loginparams;
}
