package com.caloriestracker.system.service.profile;

import com.caloriestracker.system.dto.request.profile.UserProfileRequest;
import com.caloriestracker.system.dto.response.profile.UserFullProfileResponse;
import com.caloriestracker.system.dto.response.profile.UserProfileResponse;
import com.caloriestracker.system.entity.User;
import com.caloriestracker.system.entity.UserProfile;
import com.caloriestracker.system.exception.BadRequestException;
import com.caloriestracker.system.exception.ResourceNotFoundException;
import com.caloriestracker.system.mapper.UserProfileMapper;
import com.caloriestracker.system.repository.UserProfileRepository;
import com.caloriestracker.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;
    private final UserProfileMapper profileMapper;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {

        UserProfile profile = profileRepo.findByUser_Id(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found")
                );

        return profileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(
            Long userId,
            UserProfileRequest request
    ) {

        validate(request);

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        UserProfile profile = profileRepo
                .findByUser(user)
                .orElse(new UserProfile());

        profile.setUser(user);

        profile.setGender(request.getGender());
        profile.setAge(request.getAge());
        profile.setHeightCm(request.getHeightCm());
        profile.setWeightKg(request.getWeightKg());
        profile.setGoal(request.getGoal());
        profile.setActivityLevel(request.getActivityLevel());

        UserProfile saved = profileRepo.save(profile);

        return profileMapper.toResponse(saved);
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
    }

    @Override
    @Transactional(readOnly = true)
    public UserFullProfileResponse getFullProfile(Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        UserProfile profile = profileRepo.findByUser_Id(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found")
                );

        UserProfileResponse profileResponse =
                profileMapper.toResponse(profile);

        return new UserFullProfileResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getCreatedAt(),
                profile.getGender(),
                profile.getAge(),
                profile.getHeightCm(),
                profile.getWeightKg(),
                profile.getGoal(),
                profile.getActivityLevel(),
                profileResponse.getDailyCalories()
        );
    }
}