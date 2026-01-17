package org.example.csc380_l1.service;

import org.example.csc380_l1.model.*;
import org.example.csc380_l1.repository.*;
import org.example.csc380_l1.exception.*;
import org.example.csc380_l1.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final UserService userService;

    @Transactional
    public GameResponseDto createGame(Long ownerId, GameCreateDto dto, Long currentUserId) {
        if (!ownerId.equals(currentUserId)) {
            throw new ForbiddenException("You can only add games to your own collection");
        }

        User owner = userService.findById(ownerId);

        Game game = new Game();
        game.setName(dto.getName());
        game.setPublisher(dto.getPublisher());
        game.setYearPublished(dto.getYearPublished());
        game.setSystem(dto.getSystem());
        game.setCondition(dto.getCondition());
        game.setPreviousOwners(dto.getPreviousOwners());
        game.setOwner(owner);

        game = gameRepository.save(game);
        return toResponseDto(game, currentUserId);
    }

    public GameResponseDto getGameById(Long gameId, Long currentUserId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));
        return toResponseDto(game, currentUserId);
    }

    public GameCollectionDto getUserGames(Long ownerId, int page, int limit, Long currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Game> gamePage = gameRepository.findByOwnerId(ownerId, pageable);

        return toCollectionDto(gamePage, currentUserId,
                "/v1/users/usr_" + ownerId + "/games");
    }

    public GameCollectionDto searchGames(String name, String publisher, String system,
                                         Integer yearPublished, GameCondition condition,
                                         int page, int limit, Long currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Game> gamePage = gameRepository.searchGames(name, publisher, system,
                yearPublished, condition, pageable);

        return toCollectionDto(gamePage, currentUserId, buildSearchUrl(name, publisher,
                system, yearPublished, condition));
    }

    @Transactional
    public GameResponseDto updateGame(Long gameId, GameUpdateDto dto, Long currentUserId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));

        if (!game.getOwner().getId().equals(currentUserId)) {
            throw new ForbiddenException("You can only update your own games");
        }

        if (dto.getName() != null) game.setName(dto.getName());
        if (dto.getPublisher() != null) game.setPublisher(dto.getPublisher());
        if (dto.getYearPublished() != null) game.setYearPublished(dto.getYearPublished());
        if (dto.getSystem() != null) game.setSystem(dto.getSystem());
        if (dto.getCondition() != null) game.setCondition(dto.getCondition());
        if (dto.getPreviousOwners() != null) game.setPreviousOwners(dto.getPreviousOwners());

        game = gameRepository.save(game);
        return toResponseDto(game, currentUserId);
    }

    @Transactional
    public void deleteGame(Long gameId, Long currentUserId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));

        if (!game.getOwner().getId().equals(currentUserId)) {
            throw new ForbiddenException("You can only delete your own games");
        }

        gameRepository.delete(game);
    }

    private GameResponseDto toResponseDto(Game game, Long currentUserId) {
        Map<String, HateoasLink> links = new HashMap<>();
        String gameId = game.getGameId();

        links.put("self", HateoasLink.of("/v1/games/" + gameId));
        links.put("owner", HateoasLink.of("/v1/users/" + game.getOwner().getUserId()));

        if (game.getOwner().getId().equals(currentUserId)) {
            links.put("update", HateoasLink.of("/v1/games/" + gameId, "PUT", "Update game"));
            links.put("delete", HateoasLink.of("/v1/games/" + gameId, "DELETE", "Delete game"));
        }

        return GameResponseDto.builder()
                .id(gameId)
                .name(game.getName())
                .publisher(game.getPublisher())
                .yearPublished(game.getYearPublished())
                .system(game.getSystem())
                .condition(game.getCondition())
                .previousOwners(game.getPreviousOwners())
                .ownerId(game.getOwner().getUserId())
                .ownerName(game.getOwner().getName())
                .createdAt(game.getCreatedAt())
                .updatedAt(game.getUpdatedAt())
                ._links(links)
                .build();
    }

    private GameCollectionDto toCollectionDto(Page<Game> gamePage, Long currentUserId, String baseUrl) {
        List<GameResponseDto> games = gamePage.getContent().stream()
                .map(game -> toResponseDto(game, currentUserId))
                .collect(Collectors.toList());

        PaginationDto pagination = PaginationDto.builder()
                .page(gamePage.getNumber() + 1)
                .limit(gamePage.getSize())
                .totalItems(gamePage.getTotalElements())
                .totalPages(gamePage.getTotalPages())
                .build();

        Map<String, HateoasLink> links = buildPaginationLinks(baseUrl, gamePage);

        return GameCollectionDto.builder()
                .games(games)
                .pagination(pagination)
                ._links(links)
                .build();
    }

    private Map<String, HateoasLink> buildPaginationLinks(String baseUrl, Page<?> page) {
        Map<String, HateoasLink> links = new HashMap<>();
        int currentPage = page.getNumber() + 1;

        links.put("self", HateoasLink.of(baseUrl + "?page=" + currentPage + "&limit=" + page.getSize()));
        links.put("first", HateoasLink.of(baseUrl + "?page=1&limit=" + page.getSize()));

        if (page.hasPrevious()) {
            links.put("prev", HateoasLink.of(baseUrl + "?page=" + (currentPage - 1) + "&limit=" + page.getSize()));
        }
        if (page.hasNext()) {
            links.put("next", HateoasLink.of(baseUrl + "?page=" + (currentPage + 1) + "&limit=" + page.getSize()));
        }

        links.put("last", HateoasLink.of(baseUrl + "?page=" + page.getTotalPages() + "&limit=" + page.getSize()));

        return links;
    }

    private String buildSearchUrl(String name, String publisher, String system,
                                  Integer yearPublished, GameCondition condition) {
        StringBuilder url = new StringBuilder("/v1/games");
        List<String> params = new ArrayList<>();

        if (name != null) params.add("name=" + name);
        if (publisher != null) params.add("publisher=" + publisher);
        if (system != null) params.add("system=" + system);
        if (yearPublished != null) params.add("yearPublished=" + yearPublished);
        if (condition != null) params.add("condition=" + condition);

        if (!params.isEmpty()) {
            url.append("?").append(String.join("&", params));
        }

        return url.toString();
    }
}
