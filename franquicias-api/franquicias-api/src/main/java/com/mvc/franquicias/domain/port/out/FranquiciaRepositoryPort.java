package com.mvc.franquicias.domain.port.out;

import com.mvc.franquicias.domain.model.Franquicia;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranquiciaRepositoryPort {

    Mono<Franquicia> guardar(Franquicia franquicia);

    Mono<Franquicia> buscarPorId(String id);

    Flux<Franquicia> buscarTodas();

    Mono<Boolean> existePorId(String id);
}
