package com.controlefinanceiro.service;

import com.controlefinanceiro.dto.TransacaoRequest;
import com.controlefinanceiro.dto.TransacaoResponse;
import com.controlefinanceiro.enums.StatusTransacao;
import com.controlefinanceiro.enums.StatusTransacaoResponse;
import com.controlefinanceiro.enums.TipoCategoria;
import com.controlefinanceiro.exception.ConflitoException;
import com.controlefinanceiro.exception.RecursoNaoEncontradoException;
import com.controlefinanceiro.exception.RegraNegocioException;
import com.controlefinanceiro.mapper.TransacaoMapper;
import com.controlefinanceiro.model.Categoria;
import com.controlefinanceiro.model.Conta;
import com.controlefinanceiro.model.Transacao;
import com.controlefinanceiro.repository.CategoriaRepository;
import com.controlefinanceiro.repository.ContaRepository;
import com.controlefinanceiro.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository repository;
    private final ContaRepository contaRepository;
    private final CategoriaRepository categoriaRepository;
    private final TransacaoMapper mapper;

    @Transactional(readOnly = true)
    public List<TransacaoResponse> findAll(
            String busca, TipoCategoria tipo, StatusTransacaoResponse status, YearMonth mesAno) {

        LocalDate inicio = null;
        LocalDate fim = null;

        if (mesAno != null) {
            inicio = LocalDate.of(mesAno.getYear(), mesAno.getMonth(), 1);
            fim = inicio.withDayOfMonth(inicio.lengthOfMonth());
        }

        List<Transacao> transacoes = repository
                .findWithParameters(busca, tipo, inicio, fim);

        return transacoes.stream()
                .map(mapper::toResponse)
                .filter(t -> status == null || t.getStatus() == status)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransacaoResponse findById(Long id) {

        Transacao transacao = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transação não existe"));

        return mapper.toResponse(transacao);
    }

    @Transactional
    public TransacaoResponse create(TransacaoRequest request) {

        Conta conta = contaRepository.findById(request.getContaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não existe"));

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não existe"));

        if (request.getTipo() != categoria.getTipo()) {
            throw new RegraNegocioException("Transação e categoria não podem ter tipos diferentes");
        }

        Transacao transacao = mapper.toTransacao(request);
        transacao.setConta(conta);
        transacao.setCategoria(categoria);

        Transacao salvo = repository.save(transacao);

        return mapper.toResponse(salvo);
    }

    @Transactional
    public TransacaoResponse update(Long id, TransacaoRequest request) {

        Transacao transacao = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transação não existe"));

        Conta conta = contaRepository.findById(request.getContaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não existe"));

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não existe"));

        if (request.getTipo() != categoria.getTipo()) {
            throw new RegraNegocioException("Transação e categoria não podem ter tipos diferentes");
        }

        transacao.setDescricao(request.getDescricao());
        transacao.setValor(request.getValor());
        transacao.setVencimento(request.getVencimento());
        transacao.setTipo(request.getTipo());
        transacao.setConta(conta);
        transacao.setCategoria(categoria);

        return mapper.toResponse(transacao);
    }

    @Transactional
    public TransacaoResponse pagar(Long id) {

        Transacao transacao = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transação não existe"));

        if (transacao.getStatus() == StatusTransacao.PAGA) {
            throw new ConflitoException("Transação já foi paga");
        }

        transacao.setStatus(StatusTransacao.PAGA);
        transacao.setDataPagamento(LocalDate.now());

        return mapper.toResponse(transacao);
    }

    @Transactional
    public void deleteById(Long id) {

        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Transação não existe");
        }

        repository.deleteById(id);
    }

}
