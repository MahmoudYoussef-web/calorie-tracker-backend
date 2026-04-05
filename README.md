<<<<<<< HEAD
# Calories Tracker Backend (AI-based) built with Spring Boot & MySQL

This project is a production-ready backend system for tracking daily calorie intake, meals, and nutrition using Spring Boot. It includes AI-based food analysis, meal tracking, and personalized calorie recommendations.

The goal of this project was to build a real-world health tracking system with clean architecture, scalable APIs, and intelligent features like image-based food recognition.

---

## Complete Tech Stack

* Java 21+
* Spring Boot
* Spring Data JPA (Hibernate)
* Spring Security
* JWT Authentication
* MySQL Database
* Maven
* Lombok
* MapStruct
* Swagger / OpenAPI
* AI Image Analysis Integration

---

## Architecture

The project follows a clean layered architecture:

Controller → Service → Repository → Entity
DTOs + Mappers are used to isolate API contracts from database models.

---

## Features Summary

### Authentication

* User Registration & Login
* JWT-based Authentication
* Secure endpoints

---

### User Profile

* Update personal data (age, weight, height, gender)
* Calculate daily calorie needs automatically
* Store fitness goals & activity levels

---

### Meals System

* Create meals (breakfast / lunch / dinner)
* Add food items to meals
* Update & delete meal items
* Track calories per meal

---

### AI Food Recognition 

* Upload food image
* Analyze food using AI
* Auto-detect:

  * Food name
  * Calories
  * Quantity
  * Confidence level

---

### Food Management

* Predefined food database
* Search foods by name
* Nutritional values (protein, carbs, fat)

---

### Daily Tracking

* Calculate daily consumed calories
* Compare with target calories
* Track remaining calories

---

### Dashboard

* Daily progress
* Weekly calories tracking
* Progress visualization

---

### Health Calculations

* BMI calculation
* Calorie deficit management

---

## Database Design

<p align="center">
  <img src="https://github.com/user-attachments/assets/2c4fa224-dc90-4ef3-bf22-2077c4a460c7" width="700"/>
</p>

This diagram represents the system database including users, meals, food items, AI image processing, calorie tracking, and user health metrics.

---

## REST API Overview

All endpoints are prefixed with:

```id="z7ldd1"
/api
```

---

### Auth

* POST /auth/register
* POST /auth/login

---

### Profile

* GET /profile
* PUT /profile

---

### Meals

* POST /meals
* GET /meals/{mealId}
* GET /meals/by-date
* GET /meals/daily-calories

---

### Meal Items

* POST /meals/{mealId}/items
* POST /meals/{mealId}/items/manual
* PUT /meals/items/{itemId}
* DELETE /meals/items/{itemId}

---

### AI Scan

* POST /scan/analyze/{mealId}
* POST /scan/retry/{imageId}
* GET /scan/status/{imageId}
* GET /scan/gallery

---

### Food

* GET /foods
* GET /foods/{id}
* GET /foods/search

---

### Dashboard

* GET /dashboard/daily
* GET /dashboard/weekly

---

### Health

* POST /bmi/calculate
* POST /deficit

---

## Key Highlights

* AI-powered food recognition
* Clean architecture with DTO separation
* JWT-based authentication
* Real-world health tracking system
* Scalable REST API design
* Advanced domain modeling

---

## How to Run

1. Clone the repository

2. Configure application.properties

```properties id="u1k7sn"
spring.datasource.url=jdbc:mysql://localhost:3306/calories_db
spring.datasource.username=your_user
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update

auth.jwt.secret=YOUR_SECRET
auth.jwt.expiration=3600000
```

3. Run the project

```bash id="iv6n8n"
mvn spring-boot:run
```

4. Open Swagger

```id="u4pm9y"
http://localhost:8080/swagger-ui/index.html
```

---

## Reflection

This project helped me:

* Build an AI-integrated backend system
* Design complex relational databases
* Handle real-world business logic
* Implement secure authentication
* Work with image processing workflows

---

## Author

Mahmoud
Backend Developer (Spring Boot)

=======
>>>>>>> eeae120 (feat: complete AI module + update meal, auth, and dashboard logic)
