package com.renzo.auth_example.auth.repositories;

import com.renzo.auth_example.auth.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserEmailAndProviderId(String email, Account.ProviderType providerId);

    Optional<Account> findByAccountIdAndProviderId(String accountId, Account.ProviderType providerId);
}
