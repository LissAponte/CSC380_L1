package org.example.csc380_l1.dto;

import org.example.csc380_l1.model.GameCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponseDto {
    private String id;
    private String name;
    private String publisher;
    private Integer yearPublished;
    private String system;
    private GameCondition condition;
    private Integer previousOwners;
    private String ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, HateoasLink> _links;
}