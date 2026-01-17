package org.example.csc380_l1.service;

import org.example.csc380_l1.dto.*;
import org.example.csc380_l1.model.*;
import org.example.csc380_l1.security.*;
import org.example.csc380_l1.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthResponseDto login(LoginRequestDto dto) {
        User user = userService.findByEmail(dto.getEmail());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        UserPrincipal userPrincipal = new UserPrincipal(user.getId(), user.getEmail(), user.getPassword());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities());

        String token = tokenProvider.generateToken(authentication);
        String userId = user.getUserId();

        Map<String, HateoasLink> links = new HashMap<>();
        links.put("self", HateoasLink.of("/v1/users/" + userId));
        links.put("games", HateoasLink.of("/v1/games"));
        links.put("myGames", HateoasLink.of("/v1/users/" + userId + "/games"));

        return AuthResponseDto.builder()
                .token(token)
                .expiresIn(tokenProvider.getExpirationSeconds())
                .userId(userId)
                ._links(links)
                .build();
    }
}
