package com.business.auth.security.service;

import com.business.auth.security.UserDetailImpl;
import com.business.auth.security.exception.InvalidInputException;
import com.business.auth.security.repository.AccountRepository;
import com.business.auth.security.request.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    public void changePassword(ChangePasswordRequest request, Principal connectedAccount) {
        var activeAccount = (UserDetailImpl) ((UsernamePasswordAuthenticationToken) connectedAccount).getPrincipal();
        if (!passwordEncoder.matches(request.getCurrentPassword(), activeAccount.getPassword())) {
            throw new InvalidInputException("Wrong current password");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new InvalidInputException("Password are not the same with confirm");
        }
        var account = accountRepository.findByEmail(connectedAccount.getName()).orElseThrow();
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);
    }
}
