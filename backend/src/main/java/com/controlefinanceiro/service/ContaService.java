package com.controlefinanceiro.service;

import com.controlefinanceiro.dto.ContaRequest;
import com.controlefinanceiro.dto.ContaResponse;
import com.controlefinanceiro.enums.TipoConta;
import com.controlefinanceiro.exception.ConflitoException;
import com.controlefinanceiro.exception.RecursoNaoEncontradoException;
import com.controlefinanceiro.mapper.ContaMapper;
import com.controlefinanceiro.model.Conta;
import com.controlefinanceiro.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository repository;
    private final ContaMapper mapper;

    @Transactional(readOnly = true)
    public List<ContaResponse> findAll(String busca) {

        List<Conta> contas = repository.findWithParameters(busca.trim());
        return contas.stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ContaResponse findById(Long id) {

        Conta conta = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não existe"));

        return mapper.toResponse(conta);
    }

    @Transactional
    public ContaResponse create(ContaRequest request) {

        if (repository.existsByNomeIgnoreCase(request.getNome())) {
            throw new ConflitoException("Já existe uma conta com esse nome");
        }

        Conta conta = mapper.toConta(request);
        Conta salvo = repository.save(conta);

        return mapper.toResponse(salvo);
    }

    @Transactional
    public ContaResponse update(Long id, ContaRequest request) {
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não existe"));

        if (repository.existsByNomeIgnoreCaseAndIdNot(request.getNome(), id)) {
            throw new ConflitoException("Já existe uma conta com esse nome");
        }

        conta.setNome(request.getNome());
        conta.setTipo(request.getTipo());
        conta.setSaldoInicial(request.getSaldoInicial());

        return mapper.toResponse(conta);
    }

    @Transactional
    public void deleteById(Long id) {
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não existe"));

        if (!conta.getTransacoes().isEmpty()) {
            throw new ConflitoException("Não é possível deletar uma conta que contenha transações");
        }

        repository.deleteById(id);
    }

}
