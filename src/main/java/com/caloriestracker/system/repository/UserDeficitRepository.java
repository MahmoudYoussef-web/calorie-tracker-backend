package com.caloriestracker.system.repository;

import com.caloriestracker.system.entity.UserDeficit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDeficitRepository
        extends JpaRepository<UserDeficit, Long> {

    Optional<UserDeficit> findByUser_Id(Long userId);

}