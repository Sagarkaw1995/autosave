package com.cctns.autosave.producer.service.persistence.entity;

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

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_uifp_saved", schema = "saveforms")
public class UifpSavedFormEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UIFP_SAVE_SRNO")
    private Long uifpSaveSrno;

    @Column(name = "UIFP_SAVED_NUM", nullable = false)
    private Long uifpSavedNum;

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

    @Column(name = "INFORMANT_NAME", length = 300)
    private String informantName;

    @Column(name = "IS_UIFP_SUBMTTD", length = 1)
    private String isUifpSubmttd;

    @Column(name = "SUBMTTD_UIFP_NUM")
    private Long submttdUifpNum;

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

    // Getters and setters can be generated with Lombok or manually added
}
