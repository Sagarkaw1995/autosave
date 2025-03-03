package com.cctns.autosave.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

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
