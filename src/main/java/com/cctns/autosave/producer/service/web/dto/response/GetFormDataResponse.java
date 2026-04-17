package com.cctns.autosave.producer.service.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetFormDataResponse {

    private String draftId;
    private LinkedHashMap<String, Object> jsonData;
}
