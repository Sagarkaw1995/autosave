package com.cctns.autosave.producer.service.web.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutosaveResponseDto {

    private String draftNumber;
    private LocalDateTime opTime;
    private String moduleName;
    private Long savedNum;
}
