package com.controlefinanceiro.repository;

import com.controlefinanceiro.enums.TipoCategoria;
import com.controlefinanceiro.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("""
        SELECT c FROM Categoria c
        WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')) 
        AND (:tipo IS NULL OR :tipo = c.tipo)
        ORDER BY c.nome
        """)
    List<Categoria> findWithParameters(String nome, TipoCategoria tipo);

    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
