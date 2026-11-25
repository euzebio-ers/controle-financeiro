package com.controlefinanceiro.mapper;

import com.controlefinanceiro.dto.DashboardAtrasadaResponse;
import com.controlefinanceiro.dto.TransacaoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DashboardMapper {

    DashboardAtrasadaResponse toAtrasadaResponse(TransacaoResponse response);
}
