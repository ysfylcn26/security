package com.business.auth.service;

import com.business.auth.security.entity.Account;
import com.business.auth.security.exception.InvalidInputException;
import com.business.auth.security.repository.AccountRepository;
import com.business.auth.security.request.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ADMIN')")
    public void createAccountByAdmin(SignUpRequest request) {
        var account = Account.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        boolean existsByEmail = accountRepository.existsByEmail(request.getEmail());
        if (existsByEmail) {
            throw new InvalidInputException("Already there is one account with this email");
        }
        accountRepository.save(account);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deletedAccountByAdmin(String email) {
        Optional<Account> account = accountRepository.findByEmail(email);
        if (account.isEmpty()) {
            throw new InvalidInputException("Cannot find account with this email");
        }
        account.get().setDeleted(true);
        accountRepository.save(account.get());
    }

}
