package com.mvc.franquicias.application.usecase;

import com.mvc.franquicias.domain.exception.FranquiciaNoEncontradaException;
import com.mvc.franquicias.domain.exception.SucursalNoEncontradaException;
import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.model.Producto;
import com.mvc.franquicias.domain.model.Sucursal;
import com.mvc.franquicias.domain.port.in.AgregarProductoUseCase;
import com.mvc.franquicias.domain.port.out.FranquiciaRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
public class AgregarProductoService implements AgregarProductoUseCase {

    private final FranquiciaRepositoryPort franquiciaRepositoryPort;

    public AgregarProductoService(FranquiciaRepositoryPort franquiciaRepositoryPort) {
        this.franquiciaRepositoryPort = franquiciaRepositoryPort;
    }

    @Override
    public Mono<Franquicia> agregarProducto(String franquiciaId, String sucursalId, String nombreProducto, int stockInicial) {
        return franquiciaRepositoryPort.buscarPorId(franquiciaId)
                .switchIfEmpty(Mono.error(new FranquiciaNoEncontradaException(franquiciaId)))
                .map(franquicia -> {
                    Sucursal sucursal = buscarSucursal(franquicia, sucursalId);
                    Producto nuevoProducto = new Producto(UUID.randomUUID().toString(), nombreProducto, stockInicial);
                    Sucursal sucursalActualizada = sucursal.agregarProducto(nuevoProducto);
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
