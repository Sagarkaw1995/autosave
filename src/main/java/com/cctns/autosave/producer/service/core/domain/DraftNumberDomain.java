package com.cctns.autosave.producer.service.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DraftNumberDomain {

    private String moduleName;
    private Long saveNum;
    private Integer shardTag;
}
