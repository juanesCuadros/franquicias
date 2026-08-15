package com.mvc.franquicias.application.usecase;

import com.mvc.franquicias.domain.exception.FranquiciaNoEncontradaException;
import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.port.in.ActualizarNombreFranquiciaUseCase;
import com.mvc.franquicias.domain.port.out.FranquiciaRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ActualizarNombreFranquiciaService implements ActualizarNombreFranquiciaUseCase {

    private final FranquiciaRepositoryPort franquiciaRepositoryPort;

    public ActualizarNombreFranquiciaService(FranquiciaRepositoryPort franquiciaRepositoryPort) {
        this.franquiciaRepositoryPort = franquiciaRepositoryPort;
    }

    @Override
    public Mono<Franquicia> actualizarNombre(String franquiciaId, String nuevoNombre) {
        return franquiciaRepositoryPort.buscarPorId(franquiciaId)
                .switchIfEmpty(Mono.error(new FranquiciaNoEncontradaException(franquiciaId)))
                .map(franquicia -> franquicia.actualizarNombre(nuevoNombre))
                .flatMap(franquiciaRepositoryPort::guardar);
    }
}
