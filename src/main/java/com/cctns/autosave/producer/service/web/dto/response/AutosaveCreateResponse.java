package com.cctns.autosave.producer.service.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutosaveCreateResponse {
    private String draftId;
    private LocalDateTime draftDateTime;
    private String draftSrno;
    private String draftNum;
    private String message;
}
