package com.cctns.autosave.producer.service.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutosaveDomain {

    private Integer psCd;
    private String loginId;


    private String moduleName;
    private Long savedNum;
    private LinkedHashMap<String, Object> jsonData;
    private String complainantDraftName;

    private String mlcType;
    private String mlcSubType;

    //Other fields :
    private String draftNumber;
    private LocalDateTime opTime;
}
