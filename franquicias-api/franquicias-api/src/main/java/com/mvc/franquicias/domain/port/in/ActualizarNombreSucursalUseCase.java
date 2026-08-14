package com.mvc.franquicias.domain.port.in;

import com.mvc.franquicias.domain.model.Franquicia;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ActualizarNombreSucursalUseCase {

    Mono<Franquicia> actualizarNombre(String franquiciaId, String sucursalId, String nuevoNombre);
}
