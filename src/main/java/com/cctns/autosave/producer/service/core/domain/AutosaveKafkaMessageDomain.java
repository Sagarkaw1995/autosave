package com.cctns.autosave.producer.service.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutosaveKafkaMessageDomain {
    private String moduleName;
    private Long savedNum;
    private LinkedHashMap<String, Object> jsonData;
    private String draftKey;
}
