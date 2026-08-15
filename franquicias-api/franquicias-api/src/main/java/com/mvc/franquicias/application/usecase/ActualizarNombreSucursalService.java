package com.mvc.franquicias.application.usecase;

import com.mvc.franquicias.domain.exception.FranquiciaNoEncontradaException;
import com.mvc.franquicias.domain.exception.SucursalNoEncontradaException;
import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.model.Sucursal;
import com.mvc.franquicias.domain.port.in.ActualizarNombreSucursalUseCase;
import com.mvc.franquicias.domain.port.out.FranquiciaRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ActualizarNombreSucursalService implements ActualizarNombreSucursalUseCase {

    private final FranquiciaRepositoryPort franquiciaRepositoryPort;

    public ActualizarNombreSucursalService(FranquiciaRepositoryPort franquiciaRepositoryPort) {
        this.franquiciaRepositoryPort = franquiciaRepositoryPort;
    }

    @Override
    public Mono<Franquicia> actualizarNombre(String franquiciaId, String sucursalId, String nuevoNombre) {
        return franquiciaRepositoryPort.buscarPorId(franquiciaId)
                .switchIfEmpty(Mono.error(new FranquiciaNoEncontradaException(franquiciaId)))
                .map(franquicia -> {
                    Sucursal sucursal = buscarSucursal(franquicia, sucursalId);
                    Sucursal sucursalActualizada = sucursal.actualizarNombre(nuevoNombre);
                    return reemplazarSucursal(franquicia, sucursalActualizada);
                })
                .flatMap(franquiciaRepositoryPort::guardar);
    }

    private Sucursal buscarSucursal(Franquicia franquicia, String sucursalId) {
        return franquicia.sucursales().stream()
                .filter(sucursal -> sucursal.id().equals(sucursalId))
                .findFirst()
                .orElseThrow(() -> new SucursalNoEncontradaException(sucursalId));
    }

    private Franquicia reemplazarSucursal(Franquicia franquicia, Sucursal sucursalActualizada) {
        List<Sucursal> sucursales = franquicia.sucursales().stream()
                .map(sucursal -> sucursal.id().equals(sucursalActualizada.id()) ? sucursalActualizada : sucursal)
                .toList();
        return new Franquicia(franquicia.id(), franquicia.nombre(), sucursales);
    }
}
