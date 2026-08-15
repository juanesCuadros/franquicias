package com.mvc.franquicias.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record ActualizarNombreRequest(@NotBlank String nombre) {
}
