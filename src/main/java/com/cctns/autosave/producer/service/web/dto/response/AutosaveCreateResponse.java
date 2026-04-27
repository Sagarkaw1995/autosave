package com.cctns.autosave.producer.service.web.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutosaveCreateResponse {
    private String draftId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SS")
    private LocalDateTime draftDateTime;
    private String draftSrno;
    private String draftNum;
    private String message;
}
