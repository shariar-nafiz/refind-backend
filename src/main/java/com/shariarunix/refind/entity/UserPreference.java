package com.shariarunix.refind.entity;

import com.shariarunix.refind.entity.enums.ContactMethod;
import com.shariarunix.refind.entity.enums.ThemePreference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_preferences")
public class UserPreference extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "email_notifications_enabled", nullable = false)
    @Builder.Default
    private boolean emailNotificationsEnabled = true;

    @Column(name = "push_notifications_enabled", nullable = false)
    @Builder.Default
    private boolean pushNotificationsEnabled = true;

    @Column(name = "match_alerts_enabled", nullable = false)
    @Builder.Default
    private boolean matchAlertsEnabled = true;

    @Column(name = "claim_alerts_enabled", nullable = false)
    @Builder.Default
    private boolean claimAlertsEnabled = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_contact_method", nullable = false, length = 30)
    @Builder.Default
    private ContactMethod preferredContactMethod = ContactMethod.EMAIL;

    @Column(name = "show_phone_on_claim_approved", nullable = false)
    @Builder.Default
    private boolean showPhoneOnClaimApproved = true;

    @Column(name = "show_email_on_claim_approved", nullable = false)
    @Builder.Default
    private boolean showEmailOnClaimApproved = true;

    @Column(name = "language", nullable = false, length = 10)
    @Builder.Default
    private String language = "en";

    @Enumerated(EnumType.STRING)
    @Column(name = "theme", nullable = false, length = 20)
    @Builder.Default
    private ThemePreference theme = ThemePreference.SYSTEM;
}
