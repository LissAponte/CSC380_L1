package org.example.csc380_l1.dto;
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
public class UserResponseDto {
    private String id;
    private String name;
    private String email;
    private String streetAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, HateoasLink> _links;
}