package com.qentelli.employeetrackingsystem.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.qentelli.employeetrackingsystem.entity.Account;
import com.qentelli.employeetrackingsystem.entity.CustomUserDetails;
import com.qentelli.employeetrackingsystem.entity.Project;
import com.qentelli.employeetrackingsystem.exception.AccountNotFoundException;
import com.qentelli.employeetrackingsystem.exception.DuplicateAccountException;
import com.qentelli.employeetrackingsystem.models.client.request.AccountDetailsDto;
import com.qentelli.employeetrackingsystem.repository.AccountRepository;
import com.qentelli.employeetrackingsystem.repository.ProjectRepository;
import com.qentelli.employeetrackingsystem.service.AccountService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final String ACCOUNT_NOT_FOUND = "Account not found with id: ";

    private final AccountRepository accountRepository;
    //private final PersonRepository personRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    // CREATE
    @Override
    public Account createAccount(AccountDetailsDto dto) {
        if (accountRepository.existsByAccountName(dto.getAccountName())) {
            throw new DuplicateAccountException("An account with this name already exists.");
        }
        Account account = modelMapper.map(dto, Account.class);
        return accountRepository.save(account);
    }

    // READ ALL
    @Override
    public List<AccountDetailsDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(account -> modelMapper.map(account, AccountDetailsDto.class))
                .toList();
    }

    @Override
    public Page<AccountDetailsDto> getAllActiveAccounts(Pageable pageable) {
        return accountRepository.findByAccountStatusTrue(pageable)
                .map(account -> modelMapper.map(account, AccountDetailsDto.class));
    }

    // READ BY ID
    @Override
    public AccountDetailsDto getAccountById(Integer id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND + id));
        return modelMapper.map(account, AccountDetailsDto.class);
    }

    // FULL UPDATE
    @Override
    public Account updateAccount(Integer id, AccountDetailsDto dto) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND + id));

        modelMapper.map(dto, existingAccount);
        existingAccount.setAccountName(dto.getAccountName());
        existingAccount.setAccountStartDate(dto.getAccountStartDate());
        existingAccount.setAccountEndDate(dto.getAccountEndDate());
        // existingAccount.setAccountStatus(dto.getAccountStatus()); // optional
        existingAccount.setUpdatedAt(LocalDateTime.now());
        existingAccount.setUpdatedBy(getAuthenticatedUserFullName());

        return accountRepository.save(existingAccount);
    }

    // PARTIAL UPDATE
    @Override
    public Account partialUpdateAccount(Integer id, AccountDetailsDto dto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND + id));

        if (dto.getAccountName() != null)
            account.setAccountName(dto.getAccountName());
        if (dto.getAccountStartDate() != null)
            account.setAccountStartDate(dto.getAccountStartDate());
        if (dto.getAccountEndDate() != null)
            account.setAccountEndDate(dto.getAccountEndDate());
        if (dto.getAccountStatus() != null)
            account.setAccountStatus(dto.getAccountStatus());

        account.setUpdatedAt(LocalDateTime.now());
        account.setUpdatedBy(getAuthenticatedUserFullName());

        return accountRepository.save(account);
    }

    // SOFT DELETE
    @Override
    public Account softDeleteAccount(Integer id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND + id));

        account.setAccountStatus(false);
        account.setUpdatedAt(LocalDateTime.now());
        account.setUpdatedBy(getAuthenticatedUserFullName());

        return accountRepository.save(account);
    }

    // CASCADE DELETE
    @Override
    @Transactional
    public void deleteAccount(Integer id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND + id));

        if (account.getProjects() != null) {
            for (Project project : account.getProjects()) {
                project.setProjectStatus(false);
                project.setUpdatedAt(LocalDateTime.now());
                project.setUpdatedBy(getAuthenticatedUserFullName());
                projectRepository.save(project);
            }
        }

        account.setAccountStatus(false);
        account.setUpdatedAt(LocalDateTime.now());
        account.setUpdatedBy(getAuthenticatedUserFullName());
        accountRepository.save(account);
    }

    // SEARCH
    @Override
    public Page<Account> searchAccountsByExactName(String name, Pageable pageable) {
        return accountRepository.findByAccountNameContainingIgnoreCase(name, pageable);
    }

    // Helper: Authenticated user's full name
    private String getAuthenticatedUserFullName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();

            if (principal instanceof CustomUserDetails custom) {
                return custom.getFirstName() + " " + custom.getLastName();
            }
        }

        return "System";
    }
}