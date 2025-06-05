package com.medacare.backend.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.core.util.Json;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.DiscriminatorType;

@Table(name = "app_user")
@Setter
@Getter
@NoArgsConstructor
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Version
    private Long version = 1L;

    @Size(min = 3, max = 100, message = "First name must be between 3 and 100 characters")
    @NotBlank(message = "First name is required")
    private String firstName;
    @Size(min = 3, max = 100, message = "Last name must be between 3 and 100 characters")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Column(unique = true)
    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Invalid email format")
    private String email;
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character")
    @JsonIgnore
    private String password;
    
    private boolean passwordResetRequired = false;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long institutionId;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String institutionName;

    private Boolean firstLogin = true;
    private boolean verified = false;
    @JsonIgnore
    private String verificationCode;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Object entity;

    @NotNull(message = "User Registration Origin is required")
    @Enumerated(EnumType.STRING)
    private UserOrigin origin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private Role role;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String roleName;

    @CreationTimestamp
    private OffsetDateTime createdAt;
    @CreationTimestamp
    private LocalDate createdOn;
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
    @UpdateTimestamp
    private LocalDate updatedOn;

    @JsonIgnore
    private boolean active = true;

    private String photoLink;

    // @JsonIgnore
    // @OneToOne(mappedBy = "user")
    // private Physician physician;

    public enum UserOrigin {
        SELF_REGISTERED,
        ORGANIZATION_CREATED,
        ADMIN_CREATED
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.getName().name());
        return List.of(authority);
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Role getRole() {
        return role;
    }

    public User setRole(Role role) {
        this.role = role;
        return this;
    }
}
