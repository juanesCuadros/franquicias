package com.mvc.franquicias.domain.exception;

public class FranquiciaNoEncontradaException extends RuntimeException {

    public FranquiciaNoEncontradaException(String franquiciaId) {
        super("Franquicia con id " + franquiciaId + " no encontrada");
    }
}
