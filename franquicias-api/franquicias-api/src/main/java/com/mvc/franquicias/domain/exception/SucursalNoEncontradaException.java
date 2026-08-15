package com.mvc.franquicias.domain.exception;

public class SucursalNoEncontradaException extends RuntimeException {

    public SucursalNoEncontradaException(String sucursalId) {
        super("Sucursal con id " + sucursalId + " no encontrada");
    }
}
