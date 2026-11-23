package com.controlefinanceiro.service;

import com.controlefinanceiro.dto.CategoriaRequest;
import com.controlefinanceiro.dto.CategoriaResponse;
import com.controlefinanceiro.enums.TipoCategoria;
import com.controlefinanceiro.exception.ConflitoException;
import com.controlefinanceiro.exception.RecursoNaoEncontradoException;
import com.controlefinanceiro.mapper.CategoriaMapper;
import com.controlefinanceiro.model.Categoria;
import com.controlefinanceiro.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> findAll(String busca, TipoCategoria tipo) {

        List<Categoria> categorias = repository.findWithParameters(busca.trim(), tipo);
        return categorias.stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse findById(Long id) {

        Categoria categoria = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não existe"));

        return mapper.toResponse(categoria);
    }

    @Transactional
    public CategoriaResponse create(CategoriaRequest request) {

        if (repository.existsByNomeIgnoreCase(request.getNome())) {
            throw new ConflitoException("Já existe uma categoria com esse nome");
        }

        Categoria categoria = mapper.toCategoria(request);
        Categoria salvo = repository.save(categoria);

        return mapper.toResponse(salvo);
    }

    @Transactional
    public CategoriaResponse update(Long id, CategoriaRequest request) {
        Categoria categoria = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não existe"));

        if (repository.existsByNomeIgnoreCaseAndIdNot(request.getNome(), id)) {
            throw new ConflitoException("Já existe uma categoria com esse nome");
        }
        if (categoria.getTipo() != request.getTipo() && !categoria.getTransacoes().isEmpty()) {
            throw new ConflitoException("Não é possível alterar o tipo de uma categoria que contenha transações");
        }

        categoria.setNome(request.getNome());
        categoria.setTipo(request.getTipo());

        return mapper.toResponse(categoria);
    }

    @Transactional
    public void deleteById(Long id) {
        Categoria categoria = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não existe"));

        if (!categoria.getTransacoes().isEmpty()) {
            throw new ConflitoException("Não é possível deletar uma categoria que contenha transações");
        }

        repository.deleteById(id);
    }

}
