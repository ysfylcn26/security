package com.business.auth.security.controller;

import com.business.auth.security.enums.Role;
import com.business.auth.security.request.SignInRequest;
import com.business.auth.security.request.SignUpRequest;
import com.business.auth.security.response.SignInResponse;
import com.business.auth.security.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/signup")
  public ResponseEntity<SignInResponse> signUp(@Valid @RequestBody SignUpRequest request) {
    request.setRole(Role.USER);
    return ResponseEntity.ok(service.signUp(request));
  }
  @PostMapping("/signin")
  public ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest request) {
    return ResponseEntity.ok(service.signIn(request));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<Void> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    service.refreshToken(request, response);
    return ResponseEntity.ok().build();
  }


}
