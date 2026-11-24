package org.crudfeed.core.service;

import org.crudfeed.core.dto.AuthResultResponseDto;
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

    public AuthResultResponseDto signUp(String email, String nickname, String displayName, String incomingHash) {
        if (nickname == null || nickname.isBlank() || displayName == null || displayName.isBlank()) {
            return new AuthResultResponseDto(false, "Nickname or display name cannot be empty!", null);
        }

        if (nickname.length() > 24) {
            return new AuthResultResponseDto(false, "Nickname too long! Max: 24 characters", null);
        }

        if (displayName != null && displayName.length() > 64) {
            return new AuthResultResponseDto(false, "Display name too long! Max: 64 characters", null);
        }

        if (userRepository.findByNickname(nickname) != null || userRepository.findByEmail(email) != null) {
            return new AuthResultResponseDto(false, "User with this email or nickname already exists!", null);
        }

        Instant now = Instant.now();
        String bcryptHash = encoder.encode(incomingHash);

        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setDisplayName(displayName);
        user.setPassword(bcryptHash);
        user.setBio("The nullable bio...");
        user.setRole(USER);
        user.setLastSeen(now);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setBanned(false);
        user.setVerifiedEmail(false);
        user.setDeleted(false);

        User saved = userRepository.signUp(user);
        UserResponseDto dto = new UserResponseDto(
                saved.getId(), saved.getEmail(), saved.getNickname(), saved.getDisplayName(), saved.getRole()
        );
        return new AuthResultResponseDto(true, null, dto);
    }

    public AuthResultResponseDto signIn(String email, String incomingHash) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return new AuthResultResponseDto(false, "User not found!", null);
        }
        if (user.isDeleted()) {
            return new AuthResultResponseDto(false, "User is deleted!", null);
        }
        if (user.isBanned()) {
            return new AuthResultResponseDto(false, "User is banned!", null);
        }
        if (!encoder.matches(incomingHash, user.getPassword())) {
            return new AuthResultResponseDto(false, "Invalid password!", null);
        }

        userRepository.updateLastSeen(user.getId(), Instant.now());
        UserResponseDto dto = new UserResponseDto(
                user.getId(), user.getEmail(), user.getNickname(), user.getDisplayName(), user.getRole()
        );
        return new AuthResultResponseDto(true, null, dto);
    }
}