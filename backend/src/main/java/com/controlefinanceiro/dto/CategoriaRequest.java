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

    @NotBlank(message = "Nome não pode ser vazio")
    @Size(max = 100, message = "O nome não pode ter mais de 100 caracteres")
    private String nome;

    @NotNull(message = "O tipo não pode ser nulo")
    private TipoCategoria tipo;

    public void setNome(String nome) {
        this.nome = nome == null ? null : nome.trim();
    }
}
