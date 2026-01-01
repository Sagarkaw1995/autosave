package com.cctns.autosave.producer.service.persistence.entity;

import com.cctns.autosave.producer.service.constants.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
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

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_gd_saved" , schema = "saveforms", catalog = "cctns_2_saved_forms")
public class GeneralDiarySaveEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GD_SAVE_SRNO")
    private Long gdSaveSrNo;

    @Column(name = "GD_SAVED_NUM", nullable = false)
    private String gdSaveNum;

    @Column(name = "LANG_CD")
    private Integer langCd;

    @Column(name = "IS_GD_SUBMTTD")
    private String isGDSubmitted;

    @Column(name = "SUBMTTD_GD_NUM")
    private String submittedGDNum;

    @Column(name = "RECORD_STATUS")
    private String recordStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "GMT+5:30")
    @Column(name = "RECORD_CREATED_ON")
    private LocalDateTime recordCreatedOn;

    @Column(name = "RECORD_CREATED_BY")
    private String recordCreatedBy;

    @Column(name = "RECORD_UPDATED_ON")
    private LocalDateTime recordUpdatedOn;

    @Column(name = "RECORD_UPDATED_BY")
    private String recordUpdatedBy;

    @Column(name = "RECORD_SYNC_FROM")
    private String recordSyncFrom;

    @Column(name = "RECORD_SYNC_ON")
    private LocalDateTime recordSyncOn;
}