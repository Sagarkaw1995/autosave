package com.cctns.autosave.producer.service.web.dto.response;

import java.util.LinkedHashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutosaveJsonResponseDto {

    private LinkedHashMap<String, Object> jsonData;
    private String moduleName;
    private Long savedNum;
}
