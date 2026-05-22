package com.cctns.autosave.producer.service.core.domain;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutosaveDomain {
    private Long staffId;
    private String loginId;
    private Integer langCd;
    private List<Integer> roleCd;   //current selected role
    private Long officeCd;
    private Integer stateCd;
    private Long stateId;
    private Integer districtCd;
    private Long districtId;
    private Integer psCd;
    private Long psId;
    private List<Integer> psIdList;
    private Integer officeTypeCd;
    private Integer rankCd;
    private Integer officeLevelCd;
    private List<Integer> allowedRoleCd;    //all available role for that user
    private Long oicStaffId;
    private String oicLoginId;


    private String draftId;
    private String moduleName; //Used for creating ,

    private Long firRegNum; //Added for final Form
    private String complainantDraftName;
    private String mlcType;
    private String mlcSubType;
    private LinkedHashMap<String, Object> jsonData;

    //Other fields :
    private String draftNumber;
    private LocalDateTime opTime;


    //Pagination
    private PageableDomain pageable;
}
