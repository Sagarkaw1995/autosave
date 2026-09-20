package com.cctns.autosave.producer.service.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@ToString
@NoArgsConstructor
public class ApiResponse<T> {
    String message;
    String status;
    Integer statusCode;
    List<String> errors;
    T data;
}