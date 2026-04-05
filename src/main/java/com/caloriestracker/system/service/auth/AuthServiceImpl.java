package com.caloriestracker.system.service.auth;

import com.caloriestracker.system.dto.request.auth.LoginRequest;
import com.caloriestracker.system.dto.request.auth.RegisterRequest;
import com.caloriestracker.system.dto.response.auth.AuthResponse;
import com.caloriestracker.system.entity.User;
import com.caloriestracker.system.exception.BadRequestException;
import com.caloriestracker.system.exception.UnauthorizedException;
import com.caloriestracker.system.mapper.AuthMapper;
import com.caloriestracker.system.repository.UserRepository;
import com.caloriestracker.system.service.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    // ================= REGISTER =================

    @Override
    public AuthResponse register(RegisterRequest request) {

        String email = request.getEmail().toLowerCase().trim();
        String username = request.getUsername().toLowerCase().trim();

        if (userRepo.existsByEmail(email)) {
            throw new BadRequestException("Email already used");
        }

        if (userRepo.existsByUsername(username)) {
            throw new BadRequestException("Username already used");
        }

        User user = User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .username(username)
                .email(email)
                .password(encoder.encode(request.getPassword()))
                .build();

        userRepo.save(user);

        return buildResponse(user);
    }

    // ================= LOGIN =================

    @Override
    public AuthResponse login(LoginRequest request) {

        String rawIdentifier = request.getIdentifier();

        if (rawIdentifier == null || rawIdentifier.isBlank()) {
            throw new BadRequestException("Username or email is required");
        }

        String identifier = rawIdentifier.trim().toLowerCase();

        User user = userRepo
                .findByEmailOrUsername(identifier, identifier)
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid credentials")
                );

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return buildResponse(user);
    }

    // ================= HELPER =================

    private AuthResponse buildResponse(User user) {

        String token = jwtService.generate(user);

        return authMapper.toResponse(user, token);
    }
}