package com.mvc.franquicias.domain.exception;

public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(String productoId) {
        super("Producto con id " + productoId + " no encontrado");
    }
}
