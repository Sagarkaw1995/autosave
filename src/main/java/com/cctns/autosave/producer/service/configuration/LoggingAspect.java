package com.cctns.autosave.producer.service.configuration;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Copyright: NCRB.
 * Project Name: CCTNS 2.0
 * Class Name: LoggingAspect.java
 * Description:  Logging AOP to log every method necessary details.
 *
 * @version: v1.0
 * @since 2025 -08-12
 */
@Aspect
@Slf4j
@Component
public class LoggingAspect {

    /**
     * Application component methods.
     */
    @Pointcut("within(com.cctns..*)")
    public void applicationComponentMethods() {}

    /**
     * Log method details object.
     *
     * @param joinPoint the join point
     * @return the object
     * @throws Throwable the throwable
     */
    @Around("applicationComponentMethods()")
    public Object logMethodDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();
        // Log method entry
        log.info("Entering: {}", methodName);
        Object result;
        try {
            result = joinPoint.proceed(); // Execute method
        } catch (Throwable ex) {
            log.error("Exception in {}: {}", methodName, ex.getMessage());
            throw ex;
        }
        // Log execution time
        long endTime = System.currentTimeMillis();
        // Log method exit
        log.info("Exiting: {}, Execution Time: {}ms", methodName, (endTime - startTime));
        return result;
    }

    /**
     * Log method exception.
     *
     * @param joinPoint the join point
     * @param ex        the ex
     */
    @AfterThrowing(pointcut = "applicationComponentMethods()", throwing = "ex")
    public void logMethodException(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().toShortString();
        log.error("Exception in {}: {}", methodName, ex.getMessage());
    }

    /*
@Before("applicationComponentMethods()")
public void logMethodEntry(JoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().toShortString();
    logger.info("Entering: {}", methodName);
}

@AfterReturning(pointcut = "applicationComponentMethods()", returning = "result")
public void logMethodExit(JoinPoint joinPoint, Object result) {
    String methodName = joinPoint.getSignature().toShortString();
    //logger.info("Exiting: {}, Result: {}", methodName, result);
    logger.info("Exiting: {}", methodName);
}

@Around("applicationComponentMethods()")
public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    long endTime = System.currentTimeMillis();
    String methodName = joinPoint.getSignature().toShortString();
    logger.info("Method: {}, Execution Time: {}ms", methodName, (endTime - startTime));
    return result;
}
*/
}
