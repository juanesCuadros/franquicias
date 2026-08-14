package com.mvc.franquicias.domain.model;

import java.util.ArrayList;
import java.util.List;

public record Franquicia(String id, String nombre, List<Sucursal> sucursales) {

    public Franquicia {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la franquicia no puede estar vacío");
        }
        sucursales = sucursales == null ? List.of() : List.copyOf(sucursales);
    }

    public Franquicia agregarSucursal(Sucursal nueva) {
        if (nueva == null) {
            throw new IllegalArgumentException("La sucursal a agregar no puede ser nula");
        }
        List<Sucursal> actualizadas = new ArrayList<>(sucursales);
        actualizadas.add(nueva);
        return new Franquicia(id, nombre, actualizadas);
    }

    public Franquicia actualizarNombre(String nuevoNombre) {
        return new Franquicia(id, nuevoNombre, sucursales);
    }
}
