package com.cctns.autosave.core.domain;

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
public class AutoSaveDomain {

    private String mlcType;
    private String mlcSubType;
    private String complainantDraftName;
   // private String arrestType;
    private String savedNum;
    private Integer langCd;
    private Integer psCd;
    private String recordCreatedBy;
    private String recordCreatedOn;
    private String recordStatus;
    private String moduleName;
    private LinkedHashMap<String, Object> jsonData;
}
