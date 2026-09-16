package com.cctns.autosave.producer.service.core.domain;

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
public class AutosaveDomain extends CommonParamsDomain {

    //Fields for creating draft and maintaining draft life-cycle
    private String draftId;
    private String moduleName;
    private String draftNumber;
    private LocalDateTime opTime;
    private LinkedHashMap<String, Object> jsonData;

    //Fir & Final Form Module Autosave Fields :
    private Long firRegNum;
    private String initiatedBy;

    //Bail-Cancellation Autosave Fields :
    private Long arrSurrSrNo;

    //Arrest Autosave Request Fields :
    private Long accusedSrno;
    private String accusedName;
    private Long accusedVid;   //Remove it later

    //MLC Autosave Fields :
    private String mlcType;
    private String mlcSubType;

    //Generic Autosave Request :
    private String complainantDraftName;
    private LocalDateTime regDate;
    private Long regNumber;

    //Pagination
    private PageableDomain pageable;
}
