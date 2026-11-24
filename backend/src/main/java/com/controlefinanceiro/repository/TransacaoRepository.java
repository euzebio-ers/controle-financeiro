package com.controlefinanceiro.repository;

import com.controlefinanceiro.enums.TipoCategoria;
import com.controlefinanceiro.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query("""
            SELECT t FROM Transacao t
            WHERE LOWER(t.descricao) LIKE LOWER(CONCAT('%', :busca, '%'))
            AND (:tipo IS NULL OR :tipo = t.tipo)
            AND (:inicio IS NULL OR t.vencimento BETWEEN :inicio AND :fim)
            ORDER BY t.vencimento
            """)
    List<Transacao> findWithParameters(
            String busca, TipoCategoria tipo, LocalDate inicio, LocalDate fim);
}
