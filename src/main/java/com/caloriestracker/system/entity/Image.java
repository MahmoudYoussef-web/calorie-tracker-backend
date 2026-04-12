package com.caloriestracker.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.caloriestracker.system.enums.ImageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "images",
        uniqueConstraints = @UniqueConstraint(columnNames = "path"),
        indexes = {
                @Index(name = "idx_image_user", columnList = "user_id"),
                @Index(name = "idx_image_uploaded_at", columnList = "uploaded_at"),
                @Index(name = "idx_image_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique=true)
    private String path;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onUpload() {
        this.uploadedAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="user_id", nullable=false)
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageStatus status = ImageStatus.PENDING;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String aiResultJson;

    @Column(nullable = false)
    private boolean favorite = false;

    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY)
    @JsonIgnore
    private MealItem mealItem;


    private Long fileSize;
    private String mimeType;
}