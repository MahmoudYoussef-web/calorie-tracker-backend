package com.caloriestracker.system.service.profile;

import com.caloriestracker.system.dto.request.profile.UserProfileRequest;
import com.caloriestracker.system.dto.response.profile.UserFullProfileResponse;
import com.caloriestracker.system.dto.response.profile.UserFullProfileResponse.ProfileExtras;
import com.caloriestracker.system.dto.response.profile.UserProfileResponse;
import com.caloriestracker.system.entity.User;
import com.caloriestracker.system.entity.UserDeficit;
import com.caloriestracker.system.entity.UserProfile;
import com.caloriestracker.system.exception.BadRequestException;
import com.caloriestracker.system.exception.ResourceNotFoundException;
import com.caloriestracker.system.mapper.UserProfileMapper;
import com.caloriestracker.system.repository.UserDeficitRepository;
import com.caloriestracker.system.repository.UserProfileRepository;
import com.caloriestracker.system.repository.UserRepository;
import com.caloriestracker.system.repository.WorkoutLogRepository;
import com.caloriestracker.system.service.workout.WorkoutServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;
    private final UserDeficitRepository deficitRepo;
    private final WorkoutLogRepository workoutLogRepo;
    private final UserProfileMapper profileMapper;

    private static final double CALORIES_PER_KG = 7700.0;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        UserProfile profile = profileRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        return profileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UserProfileRequest request) {

        validate(request);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfile profile = profileRepo.findByUser(user).orElse(new UserProfile());
        profile.setUser(user);
        profile.setGender(request.getGender());
        profile.setAge(request.getAge());
        profile.setHeightCm(request.getHeightCm());
        profile.setWeightKg(request.getWeightKg());
        profile.setGoal(request.getGoal());
        profile.setActivityLevel(request.getActivityLevel());
        profile.setTargetWeightKg(request.getTargetWeightKg());
        profile.setPreferredExercise(request.getPreferredExercise());
        profile.setWorkoutDuration(request.getWorkoutDuration());

        Integer newDays = request.getWeeklyExerciseDays();
        Integer oldDays = profile.getWeeklyExerciseDays();
        if (newDays != null && !newDays.equals(oldDays)) {
            profile.setWeeklyExerciseDays(newDays);
            profile.setExerciseWeekStartDate(LocalDate.now());
        }

        return profileMapper.toResponse(profileRepo.save(profile));
    }

    @Override
    @Transactional(readOnly = true)
    public UserFullProfileResponse getFullProfile(Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfile profile = profileRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        UserProfileResponse profileResponse = profileMapper.toResponse(profile);

        UserDeficit deficit = deficitRepo.findByUser_Id(userId).orElse(null);

        Double dailyDeficit         = null;
        Double weightLossPaceKg     = null;
        String weightLossPaceLabel  = null;
        String weightLossPaceId     = null;
        Integer timeToGoalWeeks     = null;

        if (deficit != null) {
            dailyDeficit     = deficit.getDeficitCalories() != null
                    ? -deficit.getDeficitCalories()
                    : null;
            weightLossPaceKg = deficit.getWeightLossPaceKgPerWeek();

            if (weightLossPaceKg != null) {
                if (weightLossPaceKg <= 0.3) {
                    weightLossPaceLabel = "Slow & Steady (0.25 kg/week)";
                    weightLossPaceId    = "slow";
                } else if (weightLossPaceKg <= 0.6) {
                    weightLossPaceLabel = "Moderate (0.5 kg/week)";
                    weightLossPaceId    = "moderate";
                } else {
                    weightLossPaceLabel = "Aggressive (1 kg/week)";
                    weightLossPaceId    = "aggressive";
                }
            }

            if (profile.getTargetWeightKg() != null
                    && weightLossPaceKg != null && weightLossPaceKg > 0) {
                double weightToLose = Math.abs(
                        profile.getWeightKg() - profile.getTargetWeightKg());
                if (weightToLose > 0) {
                    timeToGoalWeeks = (int) Math.ceil(weightToLose / weightLossPaceKg);
                }
            }
        }

        String bmiCategory = null;
        if (profile.getHeightCm() != null && profile.getWeightKg() != null) {
            double h   = profile.getHeightCm() / 100.0;
            double bmi = profile.getWeightKg() / (h * h);
            if (bmi < 18.5)     bmiCategory = "Underweight";
            else if (bmi < 25)  bmiCategory = "Normal Weight";
            else if (bmi < 30)  bmiCategory = "Overweight";
            else                bmiCategory = "Obese";
        }

        String weeklyExerciseDaysStr = null;
        String exerciseDaysStr       = null;
        if (profile.getWeeklyExerciseDays() != null) {
            int d = profile.getWeeklyExerciseDays();
            exerciseDaysStr       = String.valueOf(d);
            weeklyExerciseDaysStr = d + (d == 1 ? " day per week" : " days per week");
        }

        String preferredExercise = profile.getPreferredExercise() != null
                ? profile.getPreferredExercise() : "";

        String workoutDurationStr = profile.getWorkoutDuration() != null
                ? profile.getWorkoutDuration() + " min" : "";

        double goalReachTargetWeight = 0.0;
        double goalWeeklyExercise    = 0.0;

        if (profile.getWeeklyExerciseDays() != null
                && profile.getWeeklyExerciseDays() > 0
                && profile.getExerciseWeekStartDate() != null) {

            LocalDate weekStart = WorkoutServiceImpl.getCurrentWeekStart(profile);
            LocalDate weekEnd   = weekStart.plusDays(6);

            int actual = workoutLogRepo
                    .findByUserIdAndDateBetween(userId, weekStart, weekEnd)
                    .size();

            goalWeeklyExercise = Math.min(
                    ((double) actual / profile.getWeeklyExerciseDays()) * 100.0,
                    100.0
            );
        }

        String firstName   = user.getFirstName();
        String lastName    = user.getLastName();
        String displayName = firstName + " " + lastName;

        ProfileExtras extras = ProfileExtras.builder()
                .dailyCalorieGoal(profileResponse.getDailyCalories())
                .targetWeightKg(profile.getTargetWeightKg())
                .dailyDeficit(dailyDeficit)
                .weightLossPace(weightLossPaceLabel)
                .weightLossPaceId(weightLossPaceId)
                .bmiCategory(bmiCategory)
                .timeToGoalWeeks(timeToGoalWeeks)
                .weeklyExerciseDays(weeklyExerciseDaysStr)
                .exerciseDays(exerciseDaysStr)
                .preferredExercise(preferredExercise)
                .workoutDuration(workoutDurationStr)
                .goalReachTargetWeight(goalReachTargetWeight)
                .goalWeeklyExercise(goalWeeklyExercise)
                .build();

        return UserFullProfileResponse.builder()
                .id(user.getId())
                .firstName(firstName)
                .lastName(lastName)
                .displayName(displayName)
                .fullName(displayName)
                .email(user.getEmail())
                .joinedAt(user.getCreatedAt())
                .gender(profile.getGender())
                .age(profile.getAge())
                .heightCm(profile.getHeightCm())
                .currentWeightKg(profile.getWeightKg())
                .targetWeightKg(profile.getTargetWeightKg())
                .goal(profile.getGoal())
                .activityLevel(profile.getActivityLevel())
                .dailyCalorieGoal(profileResponse.getDailyCalories())
                .dailyDeficit(dailyDeficit)
                .weightLossPace(weightLossPaceLabel)
                .weightLossPaceId(weightLossPaceId)
                .bmiCategory(bmiCategory)
                .timeToGoalWeeks(timeToGoalWeeks)
                .weeklyExerciseDays(weeklyExerciseDaysStr)
                .exerciseDays(exerciseDaysStr)
                .preferredExercise(preferredExercise)
                .workoutDuration(workoutDurationStr)
                .goalReachTargetWeight(goalReachTargetWeight)
                .goalWeeklyExercise(goalWeeklyExercise)
                .profileExtras(extras)
                .build();
    }

    private void validate(UserProfileRequest r) {
        if (r.getHeightCm() == null || r.getHeightCm() < 50 || r.getHeightCm() > 250)
            throw new BadRequestException("Invalid height");
        if (r.getWeightKg() == null || r.getWeightKg() < 20 || r.getWeightKg() > 300)
            throw new BadRequestException("Invalid weight");
        if (r.getAge() == null || r.getAge() < 1 || r.getAge() > 120)
            throw new BadRequestException("Invalid age");
        if (r.getGender() == null)
            throw new BadRequestException("Gender is required");
        if (r.getGoal() == null)
            throw new BadRequestException("Goal is required");
        if (r.getActivityLevel() == null)
            throw new BadRequestException("Activity level is required");
        if (r.getTargetWeightKg() != null
                && (r.getTargetWeightKg() < 20 || r.getTargetWeightKg() > 300))
            throw new BadRequestException("Invalid target weight");
        if (r.getWeeklyExerciseDays() != null
                && (r.getWeeklyExerciseDays() < 0 || r.getWeeklyExerciseDays() > 7))
            throw new BadRequestException("Weekly exercise days must be between 0 and 7");
        if (r.getWorkoutDuration() != null && r.getWorkoutDuration() < 0)
            throw new BadRequestException("Workout duration cannot be negative");
    }
}