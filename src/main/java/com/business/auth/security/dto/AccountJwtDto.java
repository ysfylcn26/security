package com.business.auth.security.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountJwtDto {

    private String jwt;
    private String email;

}
