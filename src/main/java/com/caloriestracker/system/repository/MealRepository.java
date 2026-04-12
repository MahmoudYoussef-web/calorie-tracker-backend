package com.caloriestracker.system.repository;

import com.caloriestracker.system.dto.response.dashboard.CaloriesProgressResponse;
import com.caloriestracker.system.entity.Meal;
import com.caloriestracker.system.entity.MealItem;
import com.caloriestracker.system.enums.MealType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Long> {


    @Query("""
        SELECT new com.caloriestracker.system.dto.response.dashboard.CaloriesProgressResponse(
            m.mealDate,
            SUM(i.caloriesAtTime)
        )
        FROM Meal m
        JOIN m.items i
        WHERE m.user.id = :userId
          AND m.mealDate BETWEEN :start AND :end
        GROUP BY m.mealDate
        ORDER BY m.mealDate
    """)
    List<CaloriesProgressResponse> findCaloriesProgress(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );


    List<Meal> findByUser_IdAndMealDate(
            Long userId,
            LocalDate mealDate
    );


    Optional<Meal> findByUser_IdAndMealDateAndMealType(
            Long userId,
            LocalDate mealDate,
            MealType mealType
    );


}