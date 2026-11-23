package com.controlefinanceiro.controller;

import com.controlefinanceiro.dto.ContaRequest;
import com.controlefinanceiro.dto.ContaResponse;
import com.controlefinanceiro.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService service;

    @GetMapping
    public ResponseEntity<List<ContaResponse>> findAll(
            @RequestParam(defaultValue = "") String busca) {

        List<ContaResponse> response = service.findAll(busca);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponse> findById(@PathVariable Long id) {

        ContaResponse response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ContaResponse> create(@Valid @RequestBody ContaRequest request) {

        ContaResponse response = service.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaResponse> update(
            @PathVariable Long id, @Valid @RequestBody ContaRequest request) {

        ContaResponse response = service.update(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {

        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}

