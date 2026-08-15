package com.mvc.franquicias.infrastructure.adapter.in.rest.dto;

import java.util.List;

public record SucursalResponse(String id, String nombre, List<ProductoResponse> productos) {
}
