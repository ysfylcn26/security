package com.business.auth.security;

import com.business.auth.security.repository.TokenRepository;
import com.business.auth.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsServiceImpl userDetailsService;
  private final TokenRepository tokenRepository;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
    if (request.getServletPath().contains("/api/auth")) {
      filterChain.doFilter(request, response);
      return;
    }
    jwtService.getJwtDataFormHeader(request).ifPresent( accountJwtDto -> {
      if (accountJwtDto.getEmail() != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(accountJwtDto.getEmail());
        var isTokenValid = tokenRepository.findByToken(accountJwtDto.getJwt())
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
        if (jwtService.isTokenValid(accountJwtDto.getJwt(), userDetails.getUsername()) && isTokenValid) {
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    });
    filterChain.doFilter(request, response);
  }
}
