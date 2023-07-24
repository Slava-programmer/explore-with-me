package ru.practicum.mainservice.dto.category;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class CategoryDto {
    private Long id;

    @Size(min = 1, max = 50, message = "Name should not be empty and more than 50 characters")
    @NotBlank(message = "Name cannot be empty")
    private String name;
}
