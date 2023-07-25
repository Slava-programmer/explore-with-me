package ru.practicum.mainservice.dto.compilation;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationUpdateRequest {
    private Long id;

    private Set<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Title can not be empty and consist 1-50 characters")
    private String title;

}
