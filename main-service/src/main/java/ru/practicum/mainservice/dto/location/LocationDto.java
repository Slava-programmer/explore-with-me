package ru.practicum.mainservice.dto.location;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @NotNull(message = "Annotation can not be null")
    private Float lat;

    @NotNull(message = "Annotation can not be null")
    private Float lon;
}