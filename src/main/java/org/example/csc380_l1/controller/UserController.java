package org.example.csc380_l1.controller;

import org.example.csc380_l1.service.*;
import org.example.csc380_l1.security.*;
import org.example.csc380_l1.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUser(
            @PathVariable String userId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long id = parseUserId(userId);
        UserResponseDto response = userService.getUserById(id, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserUpdateDto dto,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long id = parseUserId(userId);
        UserResponseDto response = userService.updateUser(id, dto, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    private Long parseUserId(String userId) {
        if (userId.startsWith("usr_")) {
            return Long.parseLong(userId.substring(4));
        }
        throw new IllegalArgumentException("Invalid user ID format");
    }
}
