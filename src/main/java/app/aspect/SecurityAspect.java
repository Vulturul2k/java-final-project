package app.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    @Pointcut("execution(* app.controller.AuthController.*(..))")
    public void authEndpoints() {
    }

    @Pointcut("execution(* app.controller.*.*(..)) && !authEndpoints()")
    public void securedEndpoints() {
    }

    @Before("authEndpoints()")
    public void logAuthAttempt(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String clientInfo = getClientInfo();

        logger.info("🔐 [AUTH] Încercare de {} - {}", methodName, clientInfo);
    }

    @Before("securedEndpoints()")
    public void logSecuredAccess(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String username = getCurrentUsername();

        logger.info("🔒 [ACCESS] User '{}' accesează {}.{}",
                username, className, methodName);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }

    private String getClientInfo() {
        return "User: " + getCurrentUsername();
    }
}
