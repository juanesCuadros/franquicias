package com.mvc.franquicias.domain.port.in;

import com.mvc.franquicias.domain.model.Franquicia;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface CrearFranquiciaUseCase {

    Mono<Franquicia> crearFranquicia(String nombre);
}
