package ru.practicum.mainservice.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.mainservice.dto.location.LocationDto;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Запрос на создание нового события
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventNewDto {
    @Size(min = 20, max = 2000, message = "Annotation must consist of 20 - 2000 characters")
    @NotBlank(message = "Annotation can not be empty or null")
    private String annotation;

    @NotNull(message = "Category cannot be null")
    @Positive(message = "Category id must be positive")
    private Long category;

    @Size(min = 20, max = 7000, message = "Description must consist of 20 - 2000 characters")
    @NotBlank(message = "Description can not be empty or null")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "Location cannot be null")
    private LocationDto location;

    private Boolean paid = false;

    @PositiveOrZero
    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @Size(min = 3, max = 120, message = "Title must consist of 20 - 2000 characters")
    @NotBlank(message = "Title can not be empty or null")
    private String title;

}
