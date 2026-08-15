package com.mvc.franquicias.infrastructure.adapter.in.rest;

import com.mvc.franquicias.domain.exception.FranquiciaNoEncontradaException;
import com.mvc.franquicias.domain.exception.ProductoNoEncontradoException;
import com.mvc.franquicias.domain.exception.SucursalNoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            FranquiciaNoEncontradaException.class,
            SucursalNoEncontradaException.class,
            ProductoNoEncontradoException.class
    })
    public Mono<ResponseEntity<Map<String, String>>> manejarNoEncontrado(RuntimeException ex) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", "NO_ENCONTRADO");
        body.put("mensaje", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(body));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> manejarValidacion(WebExchangeBindException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fieldError -> campos.put(fieldError.getField(), fieldError.getDefaultMessage()));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "VALIDACION");
        body.put("mensaje", "Uno o más campos son inválidos");
        body.put("campos", campos);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> manejarErrorInesperado(Exception ex) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", "ERROR_INTERNO");
        body.put("mensaje", "Ocurrió un error inesperado");
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body));
    }
}
