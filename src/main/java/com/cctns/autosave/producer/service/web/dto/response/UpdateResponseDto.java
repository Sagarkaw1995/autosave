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
public class UpdateResponseDto {

    private String draftId;
    private LocalDateTime draftUpdateDateTime;
    private String message;
}
