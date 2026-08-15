package com.mvc.franquicias.application.usecase;

import com.mvc.franquicias.domain.exception.FranquiciaNoEncontradaException;
import com.mvc.franquicias.domain.exception.ProductoNoEncontradoException;
import com.mvc.franquicias.domain.exception.SucursalNoEncontradaException;
import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.model.Producto;
import com.mvc.franquicias.domain.model.Sucursal;
import com.mvc.franquicias.domain.port.in.ActualizarNombreProductoUseCase;
import com.mvc.franquicias.domain.port.out.FranquiciaRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ActualizarNombreProductoService implements ActualizarNombreProductoUseCase {

    private final FranquiciaRepositoryPort franquiciaRepositoryPort;

    public ActualizarNombreProductoService(FranquiciaRepositoryPort franquiciaRepositoryPort) {
        this.franquiciaRepositoryPort = franquiciaRepositoryPort;
    }

    @Override
    public Mono<Franquicia> actualizarNombre(String franquiciaId, String sucursalId, String productoId, String nuevoNombre) {
        return franquiciaRepositoryPort.buscarPorId(franquiciaId)
                .switchIfEmpty(Mono.error(new FranquiciaNoEncontradaException(franquiciaId)))
                .map(franquicia -> {
                    Sucursal sucursal = buscarSucursal(franquicia, sucursalId);
                    Producto producto = buscarProducto(sucursal, productoId);
                    Producto productoActualizado = producto.actualizarNombre(nuevoNombre);
                    Sucursal sucursalActualizada = reemplazarProducto(sucursal, productoActualizado);
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

    private Producto buscarProducto(Sucursal sucursal, String productoId) {
        return sucursal.productos().stream()
                .filter(producto -> producto.id().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new ProductoNoEncontradoException(productoId));
    }

    private Sucursal reemplazarProducto(Sucursal sucursal, Producto productoActualizado) {
        List<Producto> productos = sucursal.productos().stream()
                .map(producto -> producto.id().equals(productoActualizado.id()) ? productoActualizado : producto)
                .toList();
        return new Sucursal(sucursal.id(), sucursal.nombre(), productos);
    }

    private Franquicia reemplazarSucursal(Franquicia franquicia, Sucursal sucursalActualizada) {
        List<Sucursal> sucursales = franquicia.sucursales().stream()
                .map(sucursal -> sucursal.id().equals(sucursalActualizada.id()) ? sucursalActualizada : sucursal)
                .toList();
        return new Franquicia(franquicia.id(), franquicia.nombre(), sucursales);
    }
}
