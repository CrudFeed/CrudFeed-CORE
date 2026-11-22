package org.crudfeed.core.service;

import org.crudfeed.core.dto.UserResponseDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.crudfeed.core.entity.User;
import org.crudfeed.core.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static org.crudfeed.core.entity.Role.*;

@Service

public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder();
    }

    public User signUp(String email, String nickname, String displayName, String incomingHash) {
        if (userRepository.findByNickname(nickname) != null || userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Error: User with this email or nickname already exists!");
        }

        String bcryptHash = encoder.encode(incomingHash);

        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setDisplayName(displayName);
        user.setPassword(bcryptHash);
        user.setBio("The nullable bio...");
        user.setRole(USER);
        user.setLastSeen(Instant.now());
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user.setBanned(false);
        user.setVerifiedEmail(false);
        user.setDeleted(false);

        return userRepository.signUp(user);
    }

    public UserResponseDto signIn(String email, String incomingHash) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("Error: User not found!");
        }

        if (!encoder.matches(incomingHash, user.getPassword())) {
            throw new RuntimeException("Error: Invalid password!");
        }

        user.setLastSeen(Instant.now());

        return new UserResponseDto(user.getId(), user.getEmail(), user.getNickname(), user.getDisplayName());
    }
}