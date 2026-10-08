package com.finbank.account.controller;

import com.finbank.account.dto.AccountResponse;
import com.finbank.account.dto.AmountRequest;
import com.finbank.account.dto.BalanceResponse;
import com.finbank.account.dto.CreateAccountRequest;
import com.finbank.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.create(request);
    }

    @GetMapping
    public List<AccountResponse> findAll() {
        return accountService.findAll();
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse getByAccountNumber(@PathVariable String accountNumber) {
        return accountService.getByAccountNumber(accountNumber);
    }

    @GetMapping("/{accountNumber}/balance")
    public BalanceResponse getBalance(@PathVariable String accountNumber) {
        return accountService.getBalance(accountNumber);
    }

    @PutMapping("/{accountNumber}/debit")
    public AccountResponse debit(@PathVariable String accountNumber, @Valid @RequestBody AmountRequest request) {
        return accountService.debit(accountNumber, request.amount());
    }

    @PutMapping("/{accountNumber}/credit")
    public AccountResponse credit(@PathVariable String accountNumber, @Valid @RequestBody AmountRequest request) {
        return accountService.credit(accountNumber, request.amount());
    }
}
