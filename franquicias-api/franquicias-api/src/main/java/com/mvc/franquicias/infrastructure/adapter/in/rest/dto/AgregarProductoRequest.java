package com.mvc.franquicias.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AgregarProductoRequest(@NotBlank String nombre, @Min(0) int stock) {
}
