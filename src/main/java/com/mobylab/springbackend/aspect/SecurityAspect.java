package com.mobylab.springbackend.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspect pentru audit și securitate.
 * Logează cine accesează endpoint-urile securizate.
 */
@Aspect
@Component
public class SecurityAspect {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    /**
     * Pointcut pentru metodele din AuthController.
     */
    @Pointcut("execution(* com.mobylab.springbackend.controller.AuthController.*(..))")
    public void authEndpoints() {
    }

    /**
     * Pointcut pentru toate endpoint-urile securizate.
     */
    @Pointcut("execution(* com.mobylab.springbackend.controller.*.*(..)) && !authEndpoints()")
    public void securedEndpoints() {
    }

    /**
     * Logează încercările de autentificare.
     */
    @Before("authEndpoints()")
    public void logAuthAttempt(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String clientInfo = getClientInfo();

        logger.info("🔐 [AUTH] Încercare de {} - {}", methodName, clientInfo);
    }

    /**
     * Logează accesul la endpoint-uri securizate.
     */
    @Before("securedEndpoints()")
    public void logSecuredAccess(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String username = getCurrentUsername();

        logger.info("🔒 [ACCESS] User '{}' accesează {}.{}",
                username, className, methodName);
    }

    /**
     * Obține username-ul utilizatorului curent din SecurityContext.
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }

    /**
     * Obține informații despre client (poate fi extins pentru IP, user-agent,
     * etc.).
     */
    private String getClientInfo() {
        return "User: " + getCurrentUsername();
    }
}
