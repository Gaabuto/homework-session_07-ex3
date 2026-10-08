package com.finbank.transaction.controller;

import com.finbank.transaction.dto.TransactionResponse;
import com.finbank.transaction.dto.TransferRequest;
import com.finbank.transaction.dto.TransferResult;
import com.finbank.transaction.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransferService transferService;

    public TransactionController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        TransferResult result = transferService.transfer(request);
        return ResponseEntity.status(result.httpStatus()).body(result.transaction());
    }

    @GetMapping
    public List<TransactionResponse> findAll() {
        return transferService.findAll();
    }

    @GetMapping("/{id}")
    public TransactionResponse findById(@PathVariable Long id) {
        return transferService.findById(id);
    }
}
