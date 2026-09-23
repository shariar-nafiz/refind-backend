package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.auth.AuthResponse;
import com.shariarunix.refind.dto.auth.LoginRequest;
import com.shariarunix.refind.dto.auth.RegisterRequest;
import com.shariarunix.refind.dto.auth.RegisterResponse;
import com.shariarunix.refind.dto.auth.ResendOtpRequest;
import com.shariarunix.refind.dto.auth.VerifyEmailRequest;
import com.shariarunix.refind.entity.User;
import com.shariarunix.refind.entity.enums.Role;
import com.shariarunix.refind.entity.enums.UserStatus;
import com.shariarunix.refind.exception.BadRequestException;
import com.shariarunix.refind.exception.UserAccountDisabledException;
import com.shariarunix.refind.repository.UserRepository;
import com.shariarunix.refind.security.JwtTokenProvider;
import com.shariarunix.refind.security.UserPrincipal;
import com.shariarunix.refind.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private RedisTokenService redisTokenService;

    @Mock
    private OtpService otpService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User samplePendingUser;
    private User sampleActiveUser;

    @BeforeEach
    void setUp() {
        samplePendingUser = User.builder()
                .email("test@example.com")
                .phone("+8801712345678")
                .fullName("Test User")
                .passwordHash("encoded_hash")
                .role(Role.ROLE_USER)
                .status(UserStatus.PENDING)
                .build();
        samplePendingUser.setId(1L);

        sampleActiveUser = User.builder()
                .email("active@example.com")
                .phone("+8801799999999")
                .fullName("Active User")
                .passwordHash("encoded_hash")
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();
        sampleActiveUser.setId(2L);
    }

    @Test
    void register_withEmail_createsPendingUser_andDispatchesOtp() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Test User")
                .email("test@example.com")
                .phone("+8801712345678")
                .password("secret123")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByPhone("+8801712345678")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded_hash");
        when(userRepository.save(any(User.class))).thenReturn(samplePendingUser);
        when(otpService.generateOtp("test@example.com")).thenReturn("123456");

        RegisterResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.isRequiresVerification()).isTrue();
        assertThat(response.getEmail()).isEqualTo("test@example.com");

        verify(otpService).generateOtp("test@example.com");
        verify(emailService).sendOtpEmail(eq("test@example.com"), eq("Test User"), eq("123456"));
    }

    @Test
    void register_withAlreadyActiveEmail_throwsBadRequestException() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Test User")
                .email("active@example.com")
                .phone("+8801700000000")
                .password("secret123")
                .build();

        when(userRepository.findByEmail("active@example.com")).thenReturn(Optional.of(sampleActiveUser));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void verifyEmail_withValidOtp_activatesUser_andReturnsTokens() {
        VerifyEmailRequest request = VerifyEmailRequest.builder()
                .email("test@example.com")
                .otp("123456")
                .build();

        when(otpService.verifyOtp("test@example.com", "123456")).thenReturn(true);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(samplePendingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(tokenProvider.generateAccessToken(eq(1L), eq("test@example.com"), eq("ROLE_USER"))).thenReturn("access_token");
        when(tokenProvider.generateRefreshToken(1L)).thenReturn("refresh_token");
        when(tokenProvider.getAccessTokenExpirationMs()).thenReturn(86400000L);
        when(tokenProvider.getRefreshTokenExpirationMs()).thenReturn(604800000L);

        AuthResponse response = authService.verifyEmail(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
        assertThat(samplePendingUser.getStatus()).isEqualTo(UserStatus.ACTIVE);

        verify(redisTokenService).storeRefreshToken(eq(1L), eq("refresh_token"), eq(604800000L));
    }

    @Test
    void resendOtp_forPendingUser_dispatchesNewOtp() {
        ResendOtpRequest request = ResendOtpRequest.builder()
                .email("test@example.com")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(samplePendingUser));
        when(otpService.generateOtp("test@example.com")).thenReturn("654321");

        authService.resendOtp(request);

        verify(otpService).generateOtp("test@example.com");
        verify(emailService).sendOtpEmail(eq("test@example.com"), eq("Test User"), eq("654321"));
    }

    @Test
    void resendOtp_forAlreadyActiveUser_throwsBadRequestException() {
        ResendOtpRequest request = ResendOtpRequest.builder()
                .email("active@example.com")
                .build();

        when(userRepository.findByEmail("active@example.com")).thenReturn(Optional.of(sampleActiveUser));

        assertThatThrownBy(() -> authService.resendOtp(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already verified");
    }

    @Test
    void login_withPendingAccount_throwsUserAccountDisabledException() {
        LoginRequest request = LoginRequest.builder()
                .identifier("test@example.com")
                .password("secret123")
                .build();

        UserPrincipal principal = new UserPrincipal(samplePendingUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "secret123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UserAccountDisabledException.class)
                .hasMessageContaining("pending email verification");
    }
}
