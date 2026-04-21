package com.cctns.autosave.producer.service.web.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.hibernate.LazyInitializationException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.cctns.autosave.producer.service.constants.Constants;
import com.cctns.autosave.producer.service.web.dto.response.ApiResponse;
import com.cctns.autosave.producer.service.web.dto.response.ValidationResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * This is the helper method
     * This method is used to build common error response
     */
    private ResponseEntity<Object> buildResponse(Exception ex, HttpStatus status, String message) {
        Throwable rootCause = getRootCause(ex);
        String rootCauseMessage = rootCause.getClass().getSimpleName() + ": " + rootCause.getMessage();

        ApiResponse<?> errorResponse = ApiResponse.builder()
                .status(String.valueOf(status.value()))
                .message(message)
                .statusCode(status.value())
                .errors(Collections.singletonList(rootCauseMessage))
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root;
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            JsonMappingException.class,
            MismatchedInputException.class
    })
    public ResponseEntity<Object> handleJsonParseExceptions(Exception ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, Constants.PARSE_MAPPING_ERRORS);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ValidationResponse<?> errorResponse = ValidationResponse.builder()
                .status(String.valueOf(HttpStatus.BAD_REQUEST)).message(Constants.VALIDATION_FAILED_EXC)
                .errors(errors).build();
        log.error("[GlobalExceptionHandler:MethodArgumentNotValidException] : error response {}",
                errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<Object> handleDbConstraintExceptions(Exception ex) {
        HttpStatus status =
                ex instanceof ConstraintViolationException ? HttpStatus.BAD_REQUEST : HttpStatus.CONFLICT;
        return buildResponse(ex, status, Constants.DATABASE_CONSTRAINTS_ERRORS);
    }

    @ExceptionHandler({
            EntityNotFoundException.class,
            NoSuchElementException.class,
            JpaObjectRetrievalFailureException.class
    })
    public ResponseEntity<Object> handleEntityNotFoundExceptions(Exception ex) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, Constants.ENTITY_NOT_FOUND_ERRORS);
    }

    @ExceptionHandler(LazyInitializationException.class)
    public ResponseEntity<Object> handleLazyInitException(LazyInitializationException ex) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, Constants.LAZY_INIT_ERRORS);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, Constants.ILLEGAL_ARGS_ERRORS);
    }

    @ExceptionHandler({
            TransactionSystemException.class,
            OptimisticLockingFailureException.class
    })
    public ResponseEntity<Object> handleTransactionExceptions(Exception ex) {
        HttpStatus status = ex instanceof OptimisticLockingFailureException ? HttpStatus.CONFLICT
                : HttpStatus.INTERNAL_SERVER_ERROR;
        String msg = ex instanceof OptimisticLockingFailureException
                ? Constants.CONCURRENT_UPDATE_CONFLICT
                : Constants.TRANSACTION_FAILURE_ERROR;
        return buildResponse(ex, status, msg);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignException(FeignException ex) {
        HttpStatus status = ex.status() == 404 ? HttpStatus.NOT_FOUND : HttpStatus.SERVICE_UNAVAILABLE;
        return buildResponse(ex, status, Constants.FEIGN_ERRORS);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return buildResponse(ex, status, Constants.FALLBACK_ERRORS);
    }
}