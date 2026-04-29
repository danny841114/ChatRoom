package com.danny.chatroom.service;

import com.danny.chatroom.entity.User;
import com.danny.chatroom.jwt.JwtUtil;
import com.danny.chatroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public String login(String account, String password) {
        User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("Password not match");
        }

        return jwtUtil.generateToken(account);
    }
}