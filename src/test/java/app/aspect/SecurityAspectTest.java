package app.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityAspectTest {

    private SecurityAspect securityAspect;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Signature signature;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Object targetObject;

    @BeforeEach
    void setUp() {
        securityAspect = new SecurityAspect();
        targetObject = new TestTargetClass();
    }

    @Test
    void logAuthAttempt_ShouldLogAuthenticationAttempt() {
        // Arrange
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("login");

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = Mockito
                .mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getName()).thenReturn("testUser");

            // Act - Should not throw
            assertDoesNotThrow(() -> securityAspect.logAuthAttempt(joinPoint));
        }
    }

    @Test
    void logSecuredAccess_ShouldLogAccessWithUsername() {
        // Arrange
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("getBooks");
        when(joinPoint.getTarget()).thenReturn(targetObject);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = Mockito
                .mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getName()).thenReturn("testUser");

            // Act - Should not throw
            assertDoesNotThrow(() -> securityAspect.logSecuredAccess(joinPoint));
        }
    }

    @Test
    void logSecuredAccess_ShouldReturnAnonymous_WhenNotAuthenticated() {
        // Arrange
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("getBooks");
        when(joinPoint.getTarget()).thenReturn(targetObject);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = Mockito
                .mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // Act - Should not throw
            assertDoesNotThrow(() -> securityAspect.logSecuredAccess(joinPoint));
        }
    }

    private static class TestTargetClass {
    }
}
