package com.caloriestracker.system.repository;

import com.caloriestracker.system.entity.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {

    @Query("""
        SELECT w FROM WorkoutLog w
        WHERE w.user.id = :userId
          AND w.workoutDate BETWEEN :start AND :end
        ORDER BY w.workoutDate DESC
    """)
    List<WorkoutLog> findByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    boolean existsByUser_IdAndWorkoutDate(Long userId, LocalDate workoutDate);

    Optional<WorkoutLog> findByUser_IdAndWorkoutDate(Long userId, LocalDate workoutDate);
}