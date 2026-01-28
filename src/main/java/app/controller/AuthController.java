package app.controller;

import app.service.AuthService;
import app.service.dto.LoginDto;
import app.service.dto.LoginResponseDto;
import app.service.dto.RegisterDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private AuthService authService;

    @org.springframework.beans.factory.annotation.Value("${token.ttl}")
    private long tokenTtl;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        logger.info("Request to register user {}", registerDto.getEmail());
        authService.register(registerDto);
        logger.info("Successfully registered user {}", registerDto.getEmail());
        return new ResponseEntity<>("User registered", HttpStatus.CREATED);
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        logger.info("Request to login for user {}", loginDto.getEmail());
        String token = authService.login(loginDto);
        logger.info("Successfully logged in user {}", loginDto.getEmail());
        return new ResponseEntity<>(new LoginResponseDto().setToken(token).setExpire(tokenTtl), HttpStatus.OK);
    }
}
