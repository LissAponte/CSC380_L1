package org.example.csc380_l1.service;


import org.example.csc380_l1.model.*;
import org.example.csc380_l1.dto.*;
import org.example.csc380_l1.repository.*;
import org.example.csc380_l1.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("A user with this email address already exists");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStreetAddress(dto.getStreetAddress());

        user = userRepository.save(user);
        return toResponseDto(user);
    }

    public UserResponseDto getUserById(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toResponseDto(user, currentUserId);
    }

    @Transactional
    public UserResponseDto updateUser(Long userId, UserUpdateDto dto, Long currentUserId) {
        if (!userId.equals(currentUserId)) {
            throw new ForbiddenException("You do not have permission to update this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getStreetAddress() != null) {
            user.setStreetAddress(dto.getStreetAddress());
        }

        user = userRepository.save(user);
        return toResponseDto(user, currentUserId);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
    }

    private UserResponseDto toResponseDto(User user) {
        return toResponseDto(user, null);
    }

    private UserResponseDto toResponseDto(User user, Long currentUserId) {
        Map<String, HateoasLink> links = new HashMap<>();
        String userId = user.getUserId();

        links.put("self", HateoasLink.of("/v1/users/" + userId));
        links.put("games", HateoasLink.of("/v1/users/" + userId + "/games"));

        if (currentUserId != null && user.getId().equals(currentUserId)) {
            links.put("update", HateoasLink.of("/v1/users/" + userId, "PUT", "Update user"));
        }

        return UserResponseDto.builder()
                .id(userId)
                .name(user.getName())
                .email(user.getEmail())
                .streetAddress(user.getStreetAddress())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                ._links(links)
                .build();
    }
}

