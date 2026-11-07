package com.freebie.authservice.service;

import com.freebie.authservice.dto.LoginRequest;
import com.freebie.authservice.dto.LoginResponse;
import com.freebie.authservice.dto.RegisterRequest;
import com.freebie.authservice.entity.User;
import com.freebie.authservice.exception.EmailAlreadyExistsException;
import com.freebie.authservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtService jwtService) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (repo.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User(email, encoder.encode(request.password()), request.role());

        try {
            repo.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException();
        }

        return new LoginResponse(jwtService.createToken(user), user.getRole().name(), 3600);
    }

    public LoginResponse login(LoginRequest request) {
        User user = repo.findByEmail(request.email())
                .filter(u -> encoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        return new LoginResponse(jwtService.createToken(user), user.getRole().name(), 3600);
    }
}
