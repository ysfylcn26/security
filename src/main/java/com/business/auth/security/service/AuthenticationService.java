package com.business.auth.security.service;

import com.business.auth.security.dto.AccountJwtDto;
import com.business.auth.security.entity.Account;
import com.business.auth.security.entity.Token;
import com.business.auth.security.exception.InvalidInputException;
import com.business.auth.security.repository.AccountRepository;
import com.business.auth.security.repository.TokenRepository;
import com.business.auth.security.request.SignInRequest;
import com.business.auth.security.request.SignUpRequest;
import com.business.auth.security.response.SignInResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

import static com.business.auth.security.enums.TokenType.BEARER;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final AccountRepository accountRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public SignInResponse signUp(SignUpRequest request) {
    var account = Account.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();
    boolean existsByEmail = accountRepository.existsByEmail(request.getEmail());
    if (existsByEmail) {
      throw new InvalidInputException("Already there is account with this email");
    }
    var savedUser = accountRepository.save(account);
    var jwtToken = jwtService.generateToken(account.getEmail());
    var refreshToken = jwtService.generateRefreshToken(account.getEmail());
    saveUserToken(savedUser, jwtToken);
    return SignInResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
  }

  public SignInResponse signIn(SignInRequest request) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    var account = accountRepository.findByEmail(request.getEmail()).orElseThrow();
    var jwtToken = jwtService.generateToken(account.getEmail());
    var refreshToken = jwtService.generateRefreshToken(account.getEmail());
    revokeAllUserTokens(account);
    saveUserToken(account, jwtToken);
    return SignInResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
  }

  private void saveUserToken(Account account, String jwtToken) {
    var token = Token.builder().account(account).token(jwtToken).tokenType(BEARER).expired(false).revoked(false).build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(Account account) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(account.getId());
    if (validUserTokens.isEmpty()) {
      return;
    }
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Optional<AccountJwtDto> jwtDataFormHeader = jwtService.getJwtDataFormHeader(request);
    if (jwtDataFormHeader.isEmpty()) {
      return;
    }
    AccountJwtDto refreshJwt = jwtDataFormHeader.get();
    if (refreshJwt.getEmail() != null) {
      var account = this.accountRepository.findByEmail(refreshJwt.getEmail()).orElseThrow();
      if (jwtService.isTokenValid(refreshJwt.getJwt(), account.getEmail())) {
        var accessToken = jwtService.generateToken(jwtDataFormHeader.get().getEmail());
        revokeAllUserTokens(account);
        saveUserToken(account, accessToken);
        var authResponse = SignInResponse.builder().accessToken(accessToken).refreshToken(jwtDataFormHeader.get().getJwt()).build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
}
