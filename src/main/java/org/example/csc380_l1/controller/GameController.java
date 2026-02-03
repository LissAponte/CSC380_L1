package org.example.csc380_l1.controller;

import org.example.csc380_l1.dto.*;
import org.example.csc380_l1.model.GameCondition;
import org.example.csc380_l1.security.UserPrincipal;
import org.example.csc380_l1.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/users/{userId}/games")
    public ResponseEntity<GameCollectionDto> getUserGames(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long ownerId = parseUserId(userId);
        GameCollectionDto response = gameService.getUserGames(ownerId, page, limit, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/games")
    public ResponseEntity<GameResponseDto> createGame(
            @PathVariable String userId,
            @Valid @RequestBody GameCreateDto dto,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long ownerId = parseUserId(userId);
        GameResponseDto response = gameService.createGame(ownerId, dto, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/games")
    public ResponseEntity<GameCollectionDto> searchGames(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String system,
            @RequestParam(required = false) Integer yearPublished,
            @RequestParam(required = false) GameCondition condition,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        GameCollectionDto response = gameService.searchGames(name, publisher, system,
                yearPublished, condition,
                page, limit, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/games/{gameId}")
    public ResponseEntity<GameResponseDto> getGame(
            @PathVariable String gameId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long id = parseGameId(gameId);
        GameResponseDto response = gameService.getGameById(id, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/games/{gameId}")
    public ResponseEntity<GameResponseDto> updateGame(
            @PathVariable String gameId,
            @Valid @RequestBody GameUpdateDto dto,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long id = parseGameId(gameId);
        GameResponseDto response = gameService.updateGame(id, dto, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/games/{gameId}")
    public ResponseEntity<Void> deleteGame(
            @PathVariable String gameId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long id = parseGameId(gameId);
        gameService.deleteGame(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    private Long parseUserId(String userId) {
        if (userId.startsWith("usr_")) {
            return Long.parseLong(userId.substring(4));
        }
        throw new IllegalArgumentException("Invalid user ID format");
    }

    private Long parseGameId(String gameId) {
        if (gameId.startsWith("game_")) {
            return Long.parseLong(gameId.substring(5));
        }
        throw new IllegalArgumentException("Invalid game ID format");
    }
}