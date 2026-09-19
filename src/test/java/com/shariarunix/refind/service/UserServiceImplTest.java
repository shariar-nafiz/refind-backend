package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.admin.AdminUserResponse;
import com.shariarunix.refind.dto.admin.UpdateUserRoleRequest;
import com.shariarunix.refind.dto.admin.UpdateUserStatusRequest;
import com.shariarunix.refind.dto.user.AccountSetupRequest;
import com.shariarunix.refind.dto.user.AddressRequest;
import com.shariarunix.refind.dto.user.AddressResponse;
import com.shariarunix.refind.dto.user.DeactivateAccountRequest;
import com.shariarunix.refind.dto.user.UpdateUserPreferencesRequest;
import com.shariarunix.refind.dto.user.UserDashboardResponse;
import com.shariarunix.refind.dto.user.UserPreferencesResponse;
import com.shariarunix.refind.dto.user.UserProfileResponse;
import com.shariarunix.refind.dto.user.UserPublicProfileResponse;
import com.shariarunix.refind.entity.User;
import com.shariarunix.refind.entity.UserAddress;
import com.shariarunix.refind.entity.UserPreference;
import com.shariarunix.refind.entity.enums.AddressType;
import com.shariarunix.refind.entity.enums.ContactMethod;
import com.shariarunix.refind.entity.enums.Role;
import com.shariarunix.refind.entity.enums.ThemePreference;
import com.shariarunix.refind.entity.enums.UserStatus;
import com.shariarunix.refind.exception.BadRequestException;
import com.shariarunix.refind.exception.ResourceNotFoundException;
import com.shariarunix.refind.repository.UserAddressRepository;
import com.shariarunix.refind.repository.UserPreferenceRepository;
import com.shariarunix.refind.repository.UserRepository;
import com.shariarunix.refind.security.JwtTokenProvider;
import com.shariarunix.refind.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAddressRepository userAddressRepository;

    @Mock
    private UserPreferenceRepository userPreferenceRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisTokenService redisTokenService;

    @Mock
    private MediaService mediaService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .email("test@example.com")
                .phone("+8801700000000")
                .fullName("Test User")
                .passwordHash("$2a$10$hashedpassword")
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .isProfileCompleted(false)
                .build();
        sampleUser.setId(1L);
        sampleUser.setCreatedAt(Instant.now());
        sampleUser.setUpdatedAt(Instant.now());
    }

    @Test
    void completeAccountSetup_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        AddressRequest addressRequest = AddressRequest.builder()
                .addressType(AddressType.HOME)
                .country("Bangladesh")
                .state("Dhaka")
                .city("Dhaka")
                .streetAddress("House 10, Road 5")
                .isDefault(true)
                .build();

        UpdateUserPreferencesRequest prefRequest = UpdateUserPreferencesRequest.builder()
                .emailNotificationsEnabled(true)
                .pushNotificationsEnabled(false)
                .preferredContactMethod(ContactMethod.PHONE)
                .build();

        AccountSetupRequest request = AccountSetupRequest.builder()
                .fullName("Updated Name")
                .bio("Software Engineer")
                .defaultAddress(addressRequest)
                .preferences(prefRequest)
                .build();

        UserAddress savedAddress = UserAddress.builder()
                .user(sampleUser)
                .country("Bangladesh")
                .city("Dhaka")
                .streetAddress("House 10, Road 5")
                .isDefault(true)
                .build();
        savedAddress.setId(50L);

        when(userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(1L)).thenReturn(new ArrayList<>());
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedAddress);
        when(userAddressRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(Optional.of(savedAddress));

        UserPreference savedPref = UserPreference.builder()
                .user(sampleUser)
                .emailNotificationsEnabled(true)
                .pushNotificationsEnabled(false)
                .preferredContactMethod(ContactMethod.PHONE)
                .build();
        savedPref.setId(60L);
        when(userPreferenceRepository.save(any(UserPreference.class))).thenReturn(savedPref);
        when(userPreferenceRepository.findByUserId(1L)).thenReturn(Optional.of(savedPref));

        UserProfileResponse response = userService.completeAccountSetup(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.isProfileCompleted()).isTrue();
        assertThat(response.getFullName()).isEqualTo("Updated Name");
        assertThat(response.getBio()).isEqualTo("Software Engineer");
        assertThat(response.getDefaultAddress()).isNotNull();
        assertThat(response.getDefaultAddress().getCity()).isEqualTo("Dhaka");
        assertThat(response.getPreferences().getPreferredContactMethod()).isEqualTo(ContactMethod.PHONE);
    }

    @Test
    void addAddress_FirstAddress_AutomaticallySetAsDefault() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(1L)).thenReturn(List.of());

        AddressRequest request = AddressRequest.builder()
                .addressType(AddressType.WORK)
                .country("USA")
                .city("San Francisco")
                .streetAddress("123 Market St")
                .isDefault(false)
                .build();

        UserAddress address = UserAddress.builder()
                .user(sampleUser)
                .addressType(AddressType.WORK)
                .country("USA")
                .city("San Francisco")
                .streetAddress("123 Market St")
                .isDefault(true)
                .build();
        address.setId(10L);

        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(address);

        AddressResponse response = userService.addAddress(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.isDefault()).isTrue();
        assertThat(response.getCity()).isEqualTo("San Francisco");
    }

    @Test
    void getPublicProfile_SafeProjection_NoPrivateContacts() {
        sampleUser.setBio("Community volunteer");
        sampleUser.setAvatarUrl("/uploads/avatars/me.jpg");
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserAddress address = UserAddress.builder()
                .city("Berlin")
                .country("Germany")
                .streetAddress("Private Street 99")
                .isDefault(true)
                .build();
        when(userAddressRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(Optional.of(address));

        UserPublicProfileResponse response = userService.getPublicProfile(1L);

        assertThat(response).isNotNull();
        assertThat(response.getFullName()).isEqualTo("Test User");
        assertThat(response.getCity()).isEqualTo("Berlin");
        assertThat(response.getCountry()).isEqualTo("Germany");
        assertThat(response.getBio()).isEqualTo("Community volunteer");
    }

    @Test
    void deactivateAccount_CorrectPassword_RevokesSessionsAndDeactivates() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("Password123", sampleUser.getPasswordHash())).thenReturn(true);
        when(jwtTokenProvider.validateToken("fake-jwt")).thenReturn(true);
        when(jwtTokenProvider.getRemainingExpirationMs("fake-jwt")).thenReturn(3600000L);

        DeactivateAccountRequest request = DeactivateAccountRequest.builder()
                .password("Password123")
                .reason("Leaving platform")
                .build();

        userService.deactivateAccount(1L, request, "Bearer fake-jwt");

        assertThat(sampleUser.getStatus()).isEqualTo(UserStatus.INACTIVE);
        verify(userRepository).save(sampleUser);
        verify(redisTokenService).revokeRefreshToken(1L);
        verify(redisTokenService).blacklistToken(eq("fake-jwt"), eq(3600000L));
    }

    @Test
    void deactivateAccount_WrongPassword_ThrowsBadRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("WrongPass", sampleUser.getPasswordHash())).thenReturn(false);

        DeactivateAccountRequest request = DeactivateAccountRequest.builder()
                .password("WrongPass")
                .build();

        assertThatThrownBy(() -> userService.deactivateAccount(1L, request, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Incorrect password");
    }

    @Test
    void updateUserStatusForAdmin_BlockUser_RevokesActiveSessions() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateUserStatusRequest request = UpdateUserStatusRequest.builder()
                .status(UserStatus.BLOCKED)
                .reason("Spamming fake items")
                .build();

        AdminUserResponse response = userService.updateUserStatusForAdmin(1L, request);

        assertThat(response.getStatus()).isEqualTo(UserStatus.BLOCKED);
        verify(redisTokenService).revokeRefreshToken(1L);
    }
}
