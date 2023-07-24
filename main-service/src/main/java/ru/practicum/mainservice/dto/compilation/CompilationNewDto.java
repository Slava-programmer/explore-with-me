package ru.practicum.mainservice.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class CompilationNewDto {
    private Set<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Title can not be empty and consist 1-50 characters")
    @NotBlank(message = "Title cannot be empty or null")
    private final String title;
}
