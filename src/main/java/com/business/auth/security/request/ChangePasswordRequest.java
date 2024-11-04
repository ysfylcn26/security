package com.business.auth.security.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordRequest {

    private String currentPassword;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String confirmationPassword;
}
