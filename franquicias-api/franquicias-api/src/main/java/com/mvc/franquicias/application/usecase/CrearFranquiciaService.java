package com.mvc.franquicias.application.usecase;

import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.port.in.CrearFranquiciaUseCase;
import com.mvc.franquicias.domain.port.out.FranquiciaRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
public class CrearFranquiciaService implements CrearFranquiciaUseCase {

    private final FranquiciaRepositoryPort franquiciaRepositoryPort;

    public CrearFranquiciaService(FranquiciaRepositoryPort franquiciaRepositoryPort) {
        this.franquiciaRepositoryPort = franquiciaRepositoryPort;
    }

    @Override
    public Mono<Franquicia> crearFranquicia(String nombre) {
        Franquicia franquicia = new Franquicia(UUID.randomUUID().toString(), nombre, List.of());
        return franquiciaRepositoryPort.guardar(franquicia);
    }
}
