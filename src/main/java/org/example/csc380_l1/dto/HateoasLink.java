package org.example.csc380_l1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HateoasLink {
    private String href;
    private String method;
    private String title;

    public static HateoasLink of(String href) {
        return HateoasLink.builder().href(href).method("GET").build();
    }

    public static HateoasLink of(String href, String method) {
        return HateoasLink.builder().href(href).method(method).build();
    }

    public static HateoasLink of(String href, String method, String title) {
        return HateoasLink.builder().href(href).method(method).title(title).build();
    }
}
