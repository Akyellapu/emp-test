package com.qentelli.employeetrackingsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.qentelli.employeetrackingsystem.entity.Account;
import com.qentelli.employeetrackingsystem.models.client.request.AccountDetailsDto;

import java.util.List;

public interface AccountService {

    // CREATE
    Account createAccount(AccountDetailsDto dto);

    // READ ALL
    List<AccountDetailsDto> getAllAccounts();

    Page<AccountDetailsDto> getAllActiveAccounts(Pageable pageable);

    // READ BY ID
    AccountDetailsDto getAccountById(Integer id);

    // FULL UPDATE
    Account updateAccount(Integer id, AccountDetailsDto dto);

    // PARTIAL UPDATE
    Account partialUpdateAccount(Integer id, AccountDetailsDto dto);

    // SOFT DELETE
    Account softDeleteAccount(Integer id);

    // CASCADE DELETE
    void deleteAccount(Integer id);

    // SEARCH
    Page<Account> searchAccountsByExactName(String name, Pageable pageable);
}