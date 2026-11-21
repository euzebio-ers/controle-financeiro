package com.controlefinanceiro.dto;

import com.controlefinanceiro.enums.TipoCategoria;
import com.controlefinanceiro.enums.TipoConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaRequest {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotNull
    private TipoCategoria tipo;
}
