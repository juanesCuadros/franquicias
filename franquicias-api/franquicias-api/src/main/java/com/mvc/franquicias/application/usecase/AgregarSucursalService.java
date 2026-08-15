package com.mvc.franquicias.application.usecase;

import com.mvc.franquicias.domain.exception.FranquiciaNoEncontradaException;
import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.model.Sucursal;
import com.mvc.franquicias.domain.port.in.AgregarSucursalUseCase;
import com.mvc.franquicias.domain.port.out.FranquiciaRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
public class AgregarSucursalService implements AgregarSucursalUseCase {

    private final FranquiciaRepositoryPort franquiciaRepositoryPort;

    public AgregarSucursalService(FranquiciaRepositoryPort franquiciaRepositoryPort) {
        this.franquiciaRepositoryPort = franquiciaRepositoryPort;
    }

    @Override
    public Mono<Franquicia> agregarSucursal(String franquiciaId, String nombreSucursal) {
        return franquiciaRepositoryPort.buscarPorId(franquiciaId)
                .switchIfEmpty(Mono.error(new FranquiciaNoEncontradaException(franquiciaId)))
                .map(franquicia -> {
                    Sucursal nueva = new Sucursal(UUID.randomUUID().toString(), nombreSucursal, List.of());
                    return franquicia.agregarSucursal(nueva);
                })
                .flatMap(franquiciaRepositoryPort::guardar);
    }
}
