package com.cctns.autosave.producer.service.web.dto.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@AllArgsConstructor
@Getter
@Builder
@ToString
public class ValidationResponse<T> {
    String message;
    String status;
    Map<String, String> errors;
    T data;
}