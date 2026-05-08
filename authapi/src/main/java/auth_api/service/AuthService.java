package auth_api.service;

import auth_api.dto.AuthResponse;
import auth_api.dto.LoginRequest;
import auth_api.dto.RegisterRequest;

import auth_api.model.RefreshToken;
import auth_api.model.Role;
import auth_api.model.User;

import auth_api.repository.RoleRepository;
import auth_api.repository.UserRepository;

import auth_api.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
    }
public AuthResponse login(LoginRequest request) {

    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() ->
                    new RuntimeException("Invalid credentials"));

    if (!passwordEncoder.matches(
            request.getPassword(),
            user.getPassword()
    )) {

        throw new RuntimeException("Invalid credentials");
    }

    String accessToken =
            jwtService.generateToken(user.getEmail());

    RefreshToken refreshToken =
            refreshTokenService.createRefreshToken(user);

    return new AuthResponse(
            accessToken,
            refreshToken.getToken()
    );
}
}