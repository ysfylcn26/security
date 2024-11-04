package com.business.auth.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {

  @Email(message = "Please provide a valid email address")
  private String email;

  @Size(min = 8, message = "Password must be at least 8 characters long")
  private String password;

}
