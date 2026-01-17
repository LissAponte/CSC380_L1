package org.example.csc380_l1.dto;

import org.example.csc380_l1.model.GameCondition;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GameCreateDto {
    @NotBlank(message = "Game name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    @NotBlank(message = "Publisher is required")
    @Size(max = 100, message = "Publisher must not exceed 100 characters")
    private String publisher;

    @NotNull(message = "Year published is required")
    @Min(value = 1970, message = "Year must be 1970 or later")
    @Max(value = 2010, message = "Year must be 2010 or earlier")
    private Integer yearPublished;

    @NotBlank(message = "System is required")
    private String system;

    @NotNull(message = "Condition is required")
    private GameCondition condition;

    @Min(value = 0, message = "Previous owners cannot be negative")
    private Integer previousOwners;
}