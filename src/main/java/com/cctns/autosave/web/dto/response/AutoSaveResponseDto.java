package com.cctns.autosave.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutoSaveResponseDto {

    private String key;
    private Map<String,Object> jsonData;
}
