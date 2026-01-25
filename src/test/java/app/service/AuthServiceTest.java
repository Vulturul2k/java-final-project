package app.service;

import app.config.security.JwtGenerator;
import app.entity.Role;
import app.entity.User;
import app.exception.BadRequestException;
import app.repository.RoleRepository;
import app.repository.UserRepository;
import app.service.dto.LoginDto;
import app.service.dto.RegisterDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtGenerator jwtGenerator;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldCreateUser_WhenEmailIsUnique() {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setPassword("password");
        registerDto.setUsername("testuser");

        when(userRepository.existsUserByEmail(registerDto.getEmail())).thenReturn(false);
        when(roleRepository.findRoleByName("USER")).thenReturn(Optional.of(new Role()));
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");

        // Act
        authService.register(registerDto);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");

        when(userRepository.existsUserByEmail(registerDto.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> authService.register(registerDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");

        Authentication authentication = mock(Authentication.class);

        when(userRepository.findUserByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtGenerator.generateToken(authentication)).thenReturn("jwt-token");

        // Act
        String token = authService.login(loginDto);

        // Assert
        assertEquals("jwt-token", token);
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("nonexistent@example.com");

        when(userRepository.findUserByEmail(loginDto.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadRequestException.class, () -> authService.login(loginDto));
    }
}
