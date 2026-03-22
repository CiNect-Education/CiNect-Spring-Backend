package com.cinect.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9@#$%!_]+$", message = "Password can only contain letters, numbers or @ # $ % ! _")
    private String password;

    @NotBlank(message = "Full name is required")
    @Pattern(regexp = "^[\\p{L}]+(?:[\\s'][\\p{L}]+)*$", message = "Full name may contain letters and spaces only")
    private String fullName;

    @JsonProperty("phoneNumber")
    @JsonAlias({"phone", "phone_number"})
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^0\\d{9}$", message = "Phone number must start with 0 and contain exactly 10 digits")
    private String phone;
}
