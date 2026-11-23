package com.controlefinanceiro.repository;

import com.controlefinanceiro.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    @Query("""
        SELECT c FROM Conta c 
        WHERE LOWER(c.nome) 
        LIKE LOWER(CONCAT('%', :busca, '%'))
        ORDER BY c.nome
        """)
    List<Conta> findWithParameters(String busca);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
    boolean existsByNomeIgnoreCase(String nome);
}
