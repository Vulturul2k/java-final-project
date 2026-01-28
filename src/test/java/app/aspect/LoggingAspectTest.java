package app.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    private LoggingAspect loggingAspect;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private Signature signature;

    private Object targetObject;

    @BeforeEach
    void setUp() {
        loggingAspect = new LoggingAspect();
        targetObject = new TestTargetClass();
    }

    @Test
    void logBefore_ShouldLogMethodEntryWithArguments() {
        // Arrange
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getTarget()).thenReturn(targetObject);
        when(joinPoint.getArgs()).thenReturn(new Object[] { "arg1", 42 });

        // Act - Should not throw
        assertDoesNotThrow(() -> loggingAspect.logBefore(joinPoint));
    }

    @Test
    void logAfterReturning_ShouldLogMethodExitWithResult() {
        // Arrange
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getTarget()).thenReturn(targetObject);

        // Act - Should not throw
        assertDoesNotThrow(() -> loggingAspect.logAfterReturning(joinPoint, "result"));
    }

    @Test
    void logAfterThrowing_ShouldLogException() {
        // Arrange
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getTarget()).thenReturn(targetObject);
        Exception exception = new RuntimeException("Test exception");

        // Act - Should not throw
        assertDoesNotThrow(() -> loggingAspect.logAfterThrowing(joinPoint, exception));
    }

    @Test
    void logExecutionTime_ShouldMeasureTimeAndReturnResult() throws Throwable {
        // Arrange
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(proceedingJoinPoint.getTarget()).thenReturn(targetObject);
        when(proceedingJoinPoint.proceed()).thenReturn("result");

        // Act
        Object result = loggingAspect.logExecutionTime(proceedingJoinPoint);

        // Assert
        assertEquals("result", result);
        verify(proceedingJoinPoint).proceed();
    }

    @Test
    void logExecutionTime_ShouldLogAndRethrowException() throws Throwable {
        // Arrange
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(proceedingJoinPoint.getTarget()).thenReturn(targetObject);
        when(proceedingJoinPoint.proceed()).thenThrow(new RuntimeException("Test exception"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loggingAspect.logExecutionTime(proceedingJoinPoint));
    }

    private static class TestTargetClass {
    }
}
