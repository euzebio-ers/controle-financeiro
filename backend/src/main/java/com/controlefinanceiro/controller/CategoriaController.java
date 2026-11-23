package com.controlefinanceiro.controller;

import com.controlefinanceiro.dto.CategoriaRequest;
import com.controlefinanceiro.dto.CategoriaResponse;
import com.controlefinanceiro.enums.TipoCategoria;
import com.controlefinanceiro.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> findAll(
            @RequestParam(defaultValue = "") String busca, @RequestParam(required = false) TipoCategoria tipo) {

        List<CategoriaResponse> response = service.findAll(busca, tipo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> findById(@PathVariable Long id) {

        CategoriaResponse response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> create(@Valid @RequestBody CategoriaRequest request) {

        CategoriaResponse response = service.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> update(
            @PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {

        CategoriaResponse response = service.update(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {

        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}

