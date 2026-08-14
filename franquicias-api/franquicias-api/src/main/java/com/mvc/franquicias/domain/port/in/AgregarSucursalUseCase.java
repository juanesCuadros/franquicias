package com.mvc.franquicias.domain.port.in;

import com.mvc.franquicias.domain.model.Franquicia;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface AgregarSucursalUseCase {

    Mono<Franquicia> agregarSucursal(String franquiciaId, String nombreSucursal);
}
