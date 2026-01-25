package app.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* app.service.*.*(..))")
    public void serviceLayer() {
    }

    @Pointcut("execution(* app.controller.*.*(..))")
    public void controllerLayer() {
    }

    @Before("serviceLayer() || controllerLayer()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        logger.info(">>> [BEFORE] {}.{}() - Argumente: {}",
                className, methodName, Arrays.toString(args));
    }

    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.info("<<< [AFTER] {}.{}() - Rezultat: {}",
                className, methodName, result);
    }

    @AfterThrowing(pointcut = "serviceLayer() || controllerLayer()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.error("!!! [EXCEPTION] {}.{}() - Eroare: {} - Mesaj: {}",
                className, methodName, exception.getClass().getSimpleName(), exception.getMessage());
    }

    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        long startTime = System.currentTimeMillis();

        try {
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
