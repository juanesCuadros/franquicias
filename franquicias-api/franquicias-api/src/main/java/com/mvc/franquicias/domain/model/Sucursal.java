package com.mvc.franquicias.domain.model;

import java.util.ArrayList;
import java.util.List;

public record Sucursal(String id, String nombre, List<Producto> productos) {

    public Sucursal {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la sucursal no puede estar vacío");
        }
        productos = productos == null ? List.of() : List.copyOf(productos);
    }

    public Sucursal agregarProducto(Producto nuevo) {
        if (nuevo == null) {
            throw new IllegalArgumentException("El producto a agregar no puede ser nulo");
        }
        List<Producto> actualizados = new ArrayList<>(productos);
        actualizados.add(nuevo);
        return new Sucursal(id, nombre, actualizados);
    }

    public Sucursal eliminarProducto(String productoId) {
        List<Producto> actualizados = productos.stream()
                .filter(producto -> !producto.id().equals(productoId))
                .toList();
        return new Sucursal(id, nombre, actualizados);
    }

    public Sucursal actualizarNombre(String nuevoNombre) {
        return new Sucursal(id, nuevoNombre, productos);
    }
}
