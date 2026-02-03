package org.example.csc380_l1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameCollectionDto {
    private List<GameResponseDto> games;
    private PaginationDto pagination;
    private Map<String, HateoasLink> _links;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationDto {
        private Integer page;
        private Integer limit;
        private Long totalItems;
        private Integer totalPages;
    }
}

