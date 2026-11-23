package com.controlefinanceiro.mapper;

import com.controlefinanceiro.dto.CategoriaRequest;
import com.controlefinanceiro.dto.CategoriaResponse;
import com.controlefinanceiro.model.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoriaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transacoes", ignore = true)
    Categoria toCategoria(CategoriaRequest request);

    CategoriaResponse toResponse(Categoria categoria);

}
