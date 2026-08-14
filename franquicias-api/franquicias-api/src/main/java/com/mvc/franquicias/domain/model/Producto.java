package com.mvc.franquicias.domain.model;

public record Producto(String id, String nombre, int stock) {

    public Producto {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock del producto no puede ser negativo");
        }
    }

    public Producto actualizarStock(int nuevoStock) {
        return new Producto(id, nombre, nuevoStock);
    }

    public Producto actualizarNombre(String nuevoNombre) {
        return new Producto(id, nuevoNombre, stock);
    }
}
