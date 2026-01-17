package org.example.csc380_l1.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @Size(min = 1, max = 200, message = "Address must be between 1 and 200 characters")
    private String streetAddress;
}