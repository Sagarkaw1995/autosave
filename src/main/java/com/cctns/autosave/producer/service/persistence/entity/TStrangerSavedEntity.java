package com.cctns.autosave.producer.service.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Copyright: EPB.
 * Project Name: CCTNS 2.0
 * Class Name: TStrangerSavedEntity.java
 * Description:
 * @version v1.0
 * @since 2025-12-11
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_stranger_saved",schema = "saveforms")
public class TStrangerSavedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "STRNGR_SAVE_SRNO")
  private Long strngrSaveSrno;

  @Column(name = "STRNGR_SAVED_NUM", nullable = false)
  private Long strngrSavedNum;

  @Column(name = "LANG_CD", nullable = false)
  private Integer langCd;

  @Column(name = "STATE_CD")
  private Integer stateCd;

  @Column(name = "STATE_NAME")
  private String stateName;

  @Column(name = "DISTRICT_CD")
  private Integer districtCd;

  @Column(name = "DISTRICT_NAME")
  private String districtName;

  @Column(name = "PS_CD")
  private Integer psCd;

  @Column(name = "PS_NAME")
  private String psName;

  @Column(name = "INFORMANT_NAME")
  private String informantName;

  @Column(name = "IS_STRNGR_SUBMTTD")
  private String isStrngrSubmttd;

  @Column(name = "SUBMTTD_STRNGR_NUM")
  private Long submttdStrngrNum;

  @Column(name = "RECORD_STATUS")
  private String recordStatus;

  @Column(name = "RECORD_CREATED_ON")
  private LocalDateTime recordCreatedOn;

  @Column(name = "RECORD_CREATED_BY")
  private String recordCreatedBy;

  @Column(name = "RECORD_UPDATED_ON")
  private LocalDateTime recordUpdatedOn;

  @Column(name = "RECORD_UPDATED_BY")
  private String recordUpdatedBy;

}
