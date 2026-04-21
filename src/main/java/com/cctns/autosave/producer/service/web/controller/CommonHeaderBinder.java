package com.cctns.autosave.producer.service.web.controller;


import java.lang.reflect.Type;
import java.util.Set;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import com.cctns.autosave.producer.service.core.exception.InvalidHeaderException;
import com.cctns.autosave.producer.service.web.dto.request.CommonParamsDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ControllerAdvice
public class CommonHeaderBinder extends RequestBodyAdviceAdapter {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    public CommonHeaderBinder(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @Override
    public boolean supports(MethodParameter methodParameter,
                            Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {

        return CommonParamsDTO.class.isAssignableFrom((Class<?>) targetType);
    }

    @Override
    public Object afterBodyRead(Object body,
                                HttpInputMessage inputMessage,
                                MethodParameter parameter,
                                Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {

        if (!(body instanceof CommonParamsDTO base)) {
            return body;
        }

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();

        String header = request.getHeader("loginparams");

        if (header == null) {
            throw new RuntimeException("Missing loginparams header");
        }

        try {
            CommonParamsDTO headerDto =
                    objectMapper.readValue(header, CommonParamsDTO.class);

            // validation
            Set<ConstraintViolation<CommonParamsDTO>> violations =
                    validator.validate(headerDto);

            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }

            // copy values
            base.setStaffId(headerDto.getStaffId());
            base.setLoginId(headerDto.getLoginId());
            base.setLangCd(headerDto.getLangCd());
            base.setRoleCd(headerDto.getRoleCd());
            base.setOfficeCd(headerDto.getOfficeCd());
            base.setStateCd(headerDto.getStateCd());
            base.setStateId(headerDto.getStateId());
            base.setDistrictId(headerDto.getDistrictId());
            base.setDistrictCd(headerDto.getDistrictCd());
            base.setPsCd(headerDto.getPsCd());
            base.setPsId(headerDto.getPsId());
            base.setPsIdList(headerDto.getPsIdList());
            base.setOfficeTypeCd(headerDto.getOfficeTypeCd());
            base.setRankCd(headerDto.getRankCd());
            base.setOfficeLevelCd(headerDto.getOfficeLevelCd());
            base.setAllowedRoleCd(headerDto.getAllowedRoleCd());
            base.setOicStaffId(headerDto.getOicStaffId());
            base.setOicLoginId(headerDto.getOicLoginId());

        } catch (JsonProcessingException e) {
            throw new InvalidHeaderException("INVALIDHEADER");
        }

        return base;
    }
}
