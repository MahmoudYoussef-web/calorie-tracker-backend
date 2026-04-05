package com.caloriestracker.system.entity;

import com.caloriestracker.system.enums.ActivityLevel;
import com.caloriestracker.system.enums.Gender;
import com.caloriestracker.system.enums.Goal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_profiles",
        indexes = @Index(name = "idx_profile_user", columnList = "user_id"),
        uniqueConstraints = @UniqueConstraint(name = "uk_profile_user", columnNames = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Min(1)
    @Max(120)
    @Column(nullable = false)
    private Integer age;

    @DecimalMin("50.0")
    @DecimalMax("250.0")
    @Column(nullable = false)
    private Double heightCm;

    @DecimalMin("20.0")
    @DecimalMax("300.0")
    @Column(nullable = false)
    private Double weightKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Goal goal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityLevel activityLevel;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}