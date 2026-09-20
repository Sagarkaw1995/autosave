package com.cctns.autosave.producer.service.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenericAutosaveRequest extends ModuleRequest {
  private String complainantDraftName;
  private Long regNumber;
  private LocalDateTime regDate;
}
