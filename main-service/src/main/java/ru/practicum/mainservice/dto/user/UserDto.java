package ru.practicum.mainservice.dto.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Email cannot empty or null")
    @Email(regexp = "[\\w._]{1,10}@[\\w]{2,}.[\\w]{2,}", message = "Email is not format as email (email@email.com)")
    private String email;

    @Size(min = 2, max = 250, message = "The name must consist of at least 2 characters and no more than 250 characters")
    @NotBlank(message = "Name cannot be empty or null")
    private String name;
}
