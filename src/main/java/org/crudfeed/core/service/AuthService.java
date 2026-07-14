package org.crudfeed.core.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.crudfeed.core.entity.User;
import org.crudfeed.core.repository.UserRepository;

public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;


    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder();
    }


    public User signUp(String nickname, String incomingHash) {

        if (userRepository.findByNickname(nickname) != null) {
            return null;
        }

        String bcryptHash = encoder.encode(incomingHash);

        User user = new User();
        user.setNickname(nickname);
        user.setPassword(bcryptHash);

        return userRepository.signUp(user);
    }


    public User signIn(String nickname, String incomingHash) {

        User user = userRepository.findByNickname(nickname);

        if (user == null) {
            return null;
        }

        if (encoder.matches(incomingHash, user.getPassword())) {
            return user;
        }

        return null;
    }
}