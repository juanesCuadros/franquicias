package com.mvc.franquicias.application.usecase;

import com.mvc.franquicias.domain.exception.FranquiciaNoEncontradaException;
import com.mvc.franquicias.domain.exception.ProductoNoEncontradoException;
import com.mvc.franquicias.domain.exception.SucursalNoEncontradaException;
import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.model.Sucursal;
import com.mvc.franquicias.domain.port.in.EliminarProductoUseCase;
import com.mvc.franquicias.domain.port.out.FranquiciaRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class EliminarProductoService implements EliminarProductoUseCase {

    private final FranquiciaRepositoryPort franquiciaRepositoryPort;

    public EliminarProductoService(FranquiciaRepositoryPort franquiciaRepositoryPort) {
        this.franquiciaRepositoryPort = franquiciaRepositoryPort;
    }

    @Override
    public Mono<Franquicia> eliminarProducto(String franquiciaId, String sucursalId, String productoId) {
        return franquiciaRepositoryPort.buscarPorId(franquiciaId)
                .switchIfEmpty(Mono.error(new FranquiciaNoEncontradaException(franquiciaId)))
                .map(franquicia -> {
                    Sucursal sucursal = buscarSucursal(franquicia, sucursalId);
                    // Decisión: eliminar un productoId que no existe en la sucursal es un error
                    // (ProductoNoEncontradoException), no una operación idempotente silenciosa.
                    // Razón: un id inexistente casi siempre delata un bug del cliente (id mal
                    // formado, ya eliminado, o de otra sucursal) y una API que responde 200/204
                    // en ese caso esconde el problema en vez de dejarlo responder 404.
                    boolean existe = sucursal.productos().stream().anyMatch(producto -> producto.id().equals(productoId));
                    if (!existe) {
                        throw new ProductoNoEncontradoException(productoId);
                    }
                    Sucursal sucursalActualizada = sucursal.eliminarProducto(productoId);
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
