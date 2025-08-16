package ru.practicum.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @Email
    @NotBlank
    @Size(min = 6, max = 254)
    private String email;

    @Size(min = 2, max = 250)
    @NotBlank
    private String name;
}
