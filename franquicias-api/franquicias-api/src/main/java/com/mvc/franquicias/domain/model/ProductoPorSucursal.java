package com.mvc.franquicias.domain.model;

public record ProductoPorSucursal(String sucursalId, String sucursalNombre, Producto producto) {

    public ProductoPorSucursal {
        if (sucursalNombre == null || sucursalNombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la sucursal no puede estar vacío");
        }
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
    }
}
