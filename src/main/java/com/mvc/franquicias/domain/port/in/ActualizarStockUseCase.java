package com.mvc.franquicias.domain.port.in;

import com.mvc.franquicias.domain.model.Franquicia;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ActualizarStockUseCase {

    Mono<Franquicia> actualizarStock(String franquiciaId, String sucursalId, String productoId, int nuevoStock);
}
