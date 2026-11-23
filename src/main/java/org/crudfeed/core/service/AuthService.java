package org.crudfeed.core.service;

import org.crudfeed.core.dto.AuthResultDto;
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

    public AuthResultDto signUp(String email, String nickname, String displayName, String incomingHash) {
        if (userRepository.findByNickname(nickname) != null || userRepository.findByEmail(email) != null) {
            return new AuthResultDto(false, "User with this email or nickname already exists!", null);
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

        User saved = userRepository.signUp(user);
        UserResponseDto dto = new UserResponseDto(
                saved.getId(), saved.getEmail(), saved.getNickname(), saved.getDisplayName(), saved.getRole()
        );
        return new AuthResultDto(true, null, dto);
    }

    public AuthResultDto signIn(String email, String incomingHash) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return new AuthResultDto(false, "User not found!", null);
        }
        if (user.isDeleted()) {
            return new AuthResultDto(false, "User is deleted!", null);
        }
        if (user.isBanned()) {
            return new AuthResultDto(false, "User is banned!", null);
        }
        if (!encoder.matches(incomingHash, user.getPassword())) {
            return new AuthResultDto(false, "Invalid password!", null);
        }

        userRepository.updateLastSeen(user.getId(), Instant.now());
        UserResponseDto dto = new UserResponseDto(
                user.getId(), user.getEmail(), user.getNickname(), user.getDisplayName(), user.getRole()
        );
        return new AuthResultDto(true, null, dto);
    }
}