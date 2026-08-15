package com.mvc.franquicias.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Min;

public record ActualizarStockRequest(@Min(0) int stock) {
}
