package reportEngine.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import reportEngine.entity.ApiResponse;
import reportEngine.security.JwtUtil;

@RestController
@RequestMapping("/auth")
@Slf4j
@Tag(name = "Auth", description = "Authorization")
public class AuthController {
    Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "user login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody Map<String, String> credentials) {
        log.info("Login attempt for user: {}", credentials.get("email"));
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Check if email and password are present
        if (username == null || password == null)
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing required fields"));

        // Generate JWT token and refresh token
        String token = jwtUtil.createToken(username, password);
        //String refreshToken = jwtUtil.createRefreshToken(user);

        ApiResponse<Map<String, Object>> response = ApiResponse.success("Login successful");
        response.setToken(token);
        //response.setRefreshToken(refreshToken);
        // return response with user data
        return ResponseEntity.ok(response);
    }

}