package com.mvc.franquicias.infrastructure.adapter.in.rest.dto;

import java.util.List;

public record FranquiciaResponse(String id, String nombre, List<SucursalResponse> sucursales) {
}
