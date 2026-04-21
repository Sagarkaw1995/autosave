package com.cctns.autosave.producer.service.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_arrest_saved", schema = "saveforms")
public class ArrestSavedFormEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ARREST_SAVE_SRNO")
    private Long arrestSaveSrno;

    @Column(name = "ARREST_SAVED_NUM", nullable = false)
    private Long arrestSavedNum;

    @Column(name = "LANG_CD", nullable = false)
    private Integer langCd;

    @Column(name = "STATE_CD")
    private Integer stateCd;

    @Column(name = "STATE_NAME", length = 100)
    private String stateName;

    @Column(name = "DISTRICT_CD")
    private Integer districtCd;

    @Column(name = "DISTRICT_NAME", length = 100)
    private String districtName;

    @Column(name = "PS_CD")
    private Integer psCd;

    @Column(name = "PS_NAME", length = 100)
    private String psName;

    @Column(name = "ACCUSED_NAME", length = 400)
    private String accusedName;

    @Column(name = "ARREST_TYPE", length = 20)
    private String arrestType;

    @Column(name = "CASE_REG_NUM", length = 30)
    private String caseRegNum;

    @Column(name = "GD_NUM", length = 30)
    private String gdNum;

    @Column(name = "IS_ARREST_SUBMTTD", length = 1)
    private String isArrestSubmttd;

    @Column(name = "SUBMTTD_ARREST_NUM")
    private Long submttdArrestNum;

    @Column(name = "RECORD_STATUS", length = 1)
    private String recordStatus;

    @Column(name = "RECORD_CREATED_ON")
    private LocalDateTime recordCreatedOn;

    @Column(name = "RECORD_CREATED_BY", length = 50)
    private String recordCreatedBy;

    @Column(name = "RECORD_UPDATED_ON")
    private LocalDateTime recordUpdatedOn;

    @Column(name = "RECORD_UPDATED_BY", length = 50)
    private String recordUpdatedBy;

    @Column(name = "RECORD_SYNC_FROM", length = 10)
    private String recordSyncFrom;

    @Column(name = "RECORD_SYNC_ON")
    private LocalDateTime recordSyncOn;

}
