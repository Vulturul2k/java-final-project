package com.mobylab.springbackend.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect pentru logarea automată a metodelor din servicii și controllere.
 * Demonstrează utilizarea AOP (Aspect-Oriented Programming) în Spring.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Pointcut pentru toate metodele din pachetul service.
     */
    @Pointcut("execution(* com.mobylab.springbackend.service.*.*(..))")
    public void serviceLayer() {
    }

    /**
     * Pointcut pentru toate metodele din pachetul controller.
     */
    @Pointcut("execution(* com.mobylab.springbackend.controller.*.*(..))")
    public void controllerLayer() {
    }

    /**
     * Advice care se execută ÎNAINTE de apelul metodei.
     * Logează numele metodei și argumentele primite.
     */
    @Before("serviceLayer() || controllerLayer()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        logger.info(">>> [BEFORE] {}.{}() - Argumente: {}",
                className, methodName, Arrays.toString(args));
    }

    /**
     * Advice care se execută DUPĂ ce metoda returnează cu succes.
     * Logează rezultatul returnat.
     */
    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.info("<<< [AFTER] {}.{}() - Rezultat: {}",
                className, methodName, result);
    }

    /**
     * Advice care se execută când o metodă aruncă o excepție.
     * Logează detaliile excepției.
     */
    @AfterThrowing(pointcut = "serviceLayer() || controllerLayer()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.error("!!! [EXCEPTION] {}.{}() - Eroare: {} - Mesaj: {}",
                className, methodName, exception.getClass().getSimpleName(), exception.getMessage());
    }

    /**
     * Advice AROUND - cel mai puternic tip.
     * Măsoară timpul de execuție al metodelor din servicii.
     */
    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        long startTime = System.currentTimeMillis();

        try {
            // Execută metoda originală
            Object result = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.info("⏱ [TIMING] {}.{}() - Timp execuție: {} ms",
                    className, methodName, duration);

            return result;
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            logger.error("⏱ [TIMING] {}.{}() - Eșuat după {} ms",
                    className, methodName, (endTime - startTime));
            throw e;
        }
    }
}
