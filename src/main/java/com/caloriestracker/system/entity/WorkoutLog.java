package com.caloriestracker.system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "workout_logs",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_workout_user_date",
                columnNames = {"user_id", "workout_date"}
        ),
        indexes = @Index(name = "idx_workout_user_date", columnList = "user_id,workout_date")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "workout_date", nullable = false)
    private LocalDate workoutDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime loggedAt;

    @PrePersist
    void onCreate() {
        loggedAt = LocalDateTime.now();
    }
}