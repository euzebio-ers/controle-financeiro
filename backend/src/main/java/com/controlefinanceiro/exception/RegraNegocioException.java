package com.controlefinanceiro.exception;

public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String message) {
        super(message);
    }

    public RegraNegocioException() {
        super("Erro de validação");
    }
}
