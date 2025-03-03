package com.cctns.autosave.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutoSaveDomain {

    private String key;
    private LinkedHashMap<String,Object> jsonData;
}
