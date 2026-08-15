package com.mvc.franquicias.application.usecase;

import com.mvc.franquicias.domain.exception.FranquiciaNoEncontradaException;
import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.model.ProductoPorSucursal;
import com.mvc.franquicias.domain.port.in.ObtenerProductoConMasStockPorSucursalUseCase;
import com.mvc.franquicias.domain.port.out.FranquiciaRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

@Service
public class ObtenerProductoConMasStockPorSucursalService implements ObtenerProductoConMasStockPorSucursalUseCase {

    private final FranquiciaRepositoryPort franquiciaRepositoryPort;

    public ObtenerProductoConMasStockPorSucursalService(FranquiciaRepositoryPort franquiciaRepositoryPort) {
        this.franquiciaRepositoryPort = franquiciaRepositoryPort;
    }

    @Override
    public Flux<ProductoPorSucursal> obtenerProductoConMasStockPorSucursal(String franquiciaId) {
        return franquiciaRepositoryPort.buscarPorId(franquiciaId)
                .switchIfEmpty(Mono.error(new FranquiciaNoEncontradaException(franquiciaId)))
                .flatMapMany(franquicia -> Flux.fromIterable(productoConMasStockPorSucursal(franquicia)));
    }

    // En caso de empate en stock, Stream.max() con Comparator.comparingInt (internamente un
    // reduce con BinaryOperator.maxBy) conserva el primer producto encontrado en el orden de la
    // lista de productos de la sucursal, no el último.
    private List<ProductoPorSucursal> productoConMasStockPorSucursal(Franquicia franquicia) {
        return franquicia.sucursales().stream()
                .flatMap(sucursal -> sucursal.productos().stream()
                        .max(Comparator.comparingInt(producto -> producto.stock()))
                        .map(producto -> new ProductoPorSucursal(sucursal.id(), sucursal.nombre(), producto))
                        .stream())
                .toList();
    }
}
