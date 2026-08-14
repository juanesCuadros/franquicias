package com.mvc.franquicias.domain.port.in;

import com.mvc.franquicias.domain.model.ProductoPorSucursal;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface ObtenerProductoConMasStockPorSucursalUseCase {

    Flux<ProductoPorSucursal> obtenerProductoConMasStockPorSucursal(String franquiciaId);
}
