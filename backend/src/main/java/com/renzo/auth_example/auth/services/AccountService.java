package com.renzo.auth_example.auth.services;

import com.renzo.auth_example.auth.models.Account;
import com.renzo.auth_example.user.models.User;
import com.renzo.auth_example.auth.repositories.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Account> findByEmailAndProviderId(String email, Account.ProviderType providerId) {
        return accountRepository.findByUserEmailAndProviderId(email, providerId);
    }

    public Account createCredentialsAccount(final User user, String password) {
        // TODO: Verify the user doesnt have a credentials account registered
        Account account = new Account(
                user,
                user.getId().toString(),
                Account.ProviderType.CREDENTIALS,
                null,
                null,
                null,
                null,
                passwordEncoder.encode(password)
        );
        return accountRepository.save(account);
    }

    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }
}
