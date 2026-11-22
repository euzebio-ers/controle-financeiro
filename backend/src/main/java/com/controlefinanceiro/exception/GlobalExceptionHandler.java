package com.controlefinanceiro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> recursoNaoEncontradoHandler(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> regraNegocioHandler(RegraNegocioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResponse> conflitoHandler(ConflitoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacaoHandler(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponse(ex.getBindingResult()
                        .getFieldErrors()
                        .getFirst()
                        .getDefaultMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErroResponse> requisicaoInvalida(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse("Argumento inválido"));
    }
}
