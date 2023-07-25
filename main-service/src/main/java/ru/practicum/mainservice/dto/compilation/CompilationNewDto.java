package ru.practicum.mainservice.dto.compilation;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationNewDto {
    private Set<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Title can not be empty and consist 1-50 characters")
    @NotBlank(message = "Title cannot be empty or null")
    private String title;
}
