package com.cctns.autosave.producer.service.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class PageDomain<T>{
    T list;
    Long totalSize;
    Long pageCount;
}
