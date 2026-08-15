package com.mvc.franquicias.domain.port.in;

import com.mvc.franquicias.domain.model.Franquicia;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface AgregarProductoUseCase {

    Mono<Franquicia> agregarProducto(String franquiciaId, String sucursalId, String nombreProducto, int stockInicial);
}
