package com.controlefinanceiro.dto;

import com.controlefinanceiro.enums.TipoCategoria;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CategoriaResponse {

    @EqualsAndHashCode.Include
    private Long id;
    private String nome;
    private TipoCategoria tipo;
}
