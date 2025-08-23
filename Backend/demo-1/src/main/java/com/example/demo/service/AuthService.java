package com.example.demo.service;


import com.example.demo.dto.auth.*;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public void register(RegisterRequest req) {
    userRepository.findByEmail(req.email()).ifPresent(u -> { throw new IllegalArgumentException("Email already registered");});
    User user = User.builder()
        .email(req.email())
        .password(passwordEncoder.encode(req.password()))
        .name(req.name())
        .role(req.role())
        .build();
    userRepository.save(user);
  }

  public LoginResponse login(LoginRequest req) {
    var user = userRepository.findByEmail(req.email()).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    if (!passwordEncoder.matches(req.password(), user.getPassword())) {
      throw new IllegalArgumentException("Invalid credentials");
    }
    String token = jwtService.generateToken(user.getEmail(), Map.of("role", user.getRole().name(), "name", user.getName(), "id", user.getId()));
    return new LoginResponse(token, user.getEmail(), user.getName(), user.getRole());
  }
}

