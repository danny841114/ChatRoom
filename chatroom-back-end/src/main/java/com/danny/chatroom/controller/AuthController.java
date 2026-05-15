package com.danny.chatroom.controller;

import com.danny.chatroom.dto.UserDetailsImpl;
import com.danny.chatroom.dto.request.LoginRequest;
import com.danny.chatroom.jwt.JwtUtil;
import com.danny.chatroom.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//        try {
//            Map<String, Object> response = authService.login(loginRequest.getAccount(), loginRequest.getPassword());
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String account = loginRequest.getAccount();
        String password = loginRequest.getPassword();

        try {
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(account, password);
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getAccount());

            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(false) // 本機開發先 false，正式 HTTPS 改 true
                    .path("/")
                    .maxAge(86400)
                    .sameSite("Lax")
                    .build();

            Map<String, Object> response = new HashMap<>();
            response.put("account", userDetails.getAccount());
            response.put("username", userDetails.getUsername());
            response.put("userId", userDetails.getId());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutApi(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/fetchMe")
    public ResponseEntity<?> fetchMe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Map<String, Object> response = new HashMap<>();
        response.put("account", userDetails.getAccount());
        response.put("username", userDetails.getUsername());
        response.put("userId", userDetails.getId());

        return ResponseEntity.ok(response);
    }
}