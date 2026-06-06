package com.caloriestracker.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CaloriesSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaloriesSystemApplication.class, args);
	}

}
