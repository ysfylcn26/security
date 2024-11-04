package com.business.auth.security.repository;

import com.business.auth.security.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

  @Query("SELECT a FROM Account a WHERE a.email = :email AND a.deleted = false")
  Optional<Account> findByEmail(@Param("email") String email);

  boolean existsByEmail(String email);

}
