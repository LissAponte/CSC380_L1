package org.example.csc380_l1.dto;

import org.example.csc380_l1.model.GameCondition;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GameUpdateDto {
    @Size(min = 1, max = 200, message = "Name must be between 1 and 200 characters")
    private String name;

    @Size(min = 1, max = 100, message = "Publisher must be between 1 and 100 characters")
    private String publisher;

    @Min(value = 1970, message = "Year must be 1970 or later")
    @Max(value = 2010, message = "Year must be 2010 or earlier")
    private Integer yearPublished;

    private String system;

    private GameCondition condition;

    @Min(value = 0, message = "Previous owners cannot be negative")
    private Integer previousOwners;
}