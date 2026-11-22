package com.controlefinanceiro.exception;

public class ConflitoException extends RuntimeException {

    public ConflitoException(String message) {
        super(message);
    }

    public ConflitoException() {
        super("Erro de conlito");
    }
}
