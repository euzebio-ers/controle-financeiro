package com.controlefinanceiro.controller;

import com.controlefinanceiro.dto.TransacaoRequest;
import com.controlefinanceiro.dto.TransacaoResponse;
import com.controlefinanceiro.enums.StatusTransacaoResponse;
import com.controlefinanceiro.enums.TipoCategoria;
import com.controlefinanceiro.service.TransacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService service;

    @GetMapping
    public ResponseEntity<List<TransacaoResponse>> findAll(
            @RequestParam(defaultValue = "") String busca,
            @RequestParam(required = false) TipoCategoria tipo,
            @RequestParam(required = false) StatusTransacaoResponse status,
            @RequestParam(required = false) YearMonth mes) {

        List<TransacaoResponse> response = service.findAll(busca, tipo, status, mes);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransacaoResponse> findById(@PathVariable Long id) {

        TransacaoResponse response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TransacaoResponse> create(@Valid @RequestBody TransacaoRequest request) {

        TransacaoResponse response = service.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransacaoResponse> update(
            @PathVariable Long id, @Valid @RequestBody TransacaoRequest request) {

        TransacaoResponse response = service.update(id, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<TransacaoResponse> pay(@PathVariable Long id) {

        TransacaoResponse pago = service.pagar(id);

        return ResponseEntity.ok(pago);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {

        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}

