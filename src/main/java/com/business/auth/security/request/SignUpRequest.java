package com.business.auth.security.request;

import com.business.auth.security.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

  @NotBlank(message = "Please provide a firstname")
  private String firstname;

  @NotBlank(message = "Please provide a lastname")
  private String lastname;

  @Email(message = "Please provide a valid email address")
  private String email;

  @Size(min = 8, message = "Password must be at least 8 characters long")
  private String password;

  @JsonIgnore
  private Role role;

}
