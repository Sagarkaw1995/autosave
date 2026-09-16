package com.cctns.autosave.producer.service.constants;

import lombok.Getter;

/**
 * Every field name that can appear in draft data are wrapped / uses
 * this data
 */
@Getter
public enum AutosaveGridField {

    // Common Autosave Fields :
    DRAFT_NUM("draftNum"),
    DRAFT_SRNO("draftSrno"),
    DRAFT_ID("draftId"),
    DRAFT_DATE_TIME("draftDateTime"),
    JSON_DATA("jsonData"),
    COMPLAINANT_NAME("complainantDraftName"),
    REG_NUMBER("regNumber"),
    REG_DATE("regDate"),
    LAST_UPDATED("lastUpdated"),

    //Fields For MLC :
    MLC_TYPE("mlcType"),
    MLC_SUB_TYPE("mlcSubType"),

    // Fir Specific Fields :
    FIR_REG_NUM("firRegNum"),

    //Bail And Arrest Specific Fields :
    ACCUSED_VID("accusedVid"),
    ACCUSED_NAME("accusedName"),
    ACCUSED_SERIAL_NUMBER("accusedSrno"),
    ARR_SURR_SR_NO("arrSurrSrNo");

    private final String key;

    AutosaveGridField(String key) {
        this.key = key;
    }
}