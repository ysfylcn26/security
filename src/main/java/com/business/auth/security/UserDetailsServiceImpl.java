package com.business.auth.security;

import com.business.auth.security.entity.Account;
import com.business.auth.security.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return UserDetailImpl.builder().username(account.getEmail()).password(account.getPassword())
                .authorities(account.getRole().getAuthorities()).build();
    }

}
