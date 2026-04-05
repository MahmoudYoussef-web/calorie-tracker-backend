package com.caloriestracker.system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_deficits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeficit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    private Double maintenanceCalories;

    private Double deficitCalories;

    private Double targetCalories;
}