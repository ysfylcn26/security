package com.business.auth.security.service;

import com.business.auth.security.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final TokenRepository tokenRepository;
  private final JwtService jwtService;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    jwtService.getJwtDataFormHeader(request).ifPresent(accountJwtDto -> {
      var storedToken = tokenRepository.findByToken(accountJwtDto.getJwt()).orElse(null);
      if (storedToken != null) {
        storedToken.setExpired(true);
        storedToken.setRevoked(true);
        tokenRepository.save(storedToken);
        SecurityContextHolder.clearContext();
      }
    });
  }

}
