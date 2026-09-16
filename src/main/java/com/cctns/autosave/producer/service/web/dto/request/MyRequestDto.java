package com.cctns.autosave.producer.service.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyRequestDto {

    /**
     * This represent the common payload :
     */
    private Integer langCd;
    private List<Integer> roleCd;   //current selected role
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
    private List<Integer> allowedRoleCd;   //all availabe role for that user
}
