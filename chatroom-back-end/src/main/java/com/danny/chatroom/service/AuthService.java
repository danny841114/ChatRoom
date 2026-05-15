package com.danny.chatroom.service;

import com.danny.chatroom.dto.UserDetailsImpl;
import com.danny.chatroom.entity.User;
import com.danny.chatroom.jwt.JwtUtil;
import com.danny.chatroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthService implements UserDetailsService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

//    public Map<String, Object> login(String account, String password) {
//        User user = userRepository.findByAccount(account)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!password.equals(user.getPassword())) {
//            throw new RuntimeException("Password not match");
//        }
//
//        String token = jwtUtil.generateToken(account);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("account", user.getAccount());
//        response.put("username", user.getUsername());
//        response.put("userId", user.getId());
//        response.put("token", token);
//
//        return response;
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByAccount(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserDetailsImpl(user);
    }
}