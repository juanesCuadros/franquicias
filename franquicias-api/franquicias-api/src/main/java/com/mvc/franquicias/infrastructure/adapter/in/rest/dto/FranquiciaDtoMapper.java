package com.mvc.franquicias.infrastructure.adapter.in.rest.dto;

import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.model.Producto;
import com.mvc.franquicias.domain.model.ProductoPorSucursal;
import com.mvc.franquicias.domain.model.Sucursal;

import java.util.List;

public final class FranquiciaDtoMapper {

    private FranquiciaDtoMapper() {
    }

    public static FranquiciaResponse toResponse(Franquicia franquicia) {
        List<SucursalResponse> sucursales = franquicia.sucursales().stream()
                .map(FranquiciaDtoMapper::toResponse)
                .toList();
        return new FranquiciaResponse(franquicia.id(), franquicia.nombre(), sucursales);
    }

    public static ProductoPorSucursalResponse toResponse(ProductoPorSucursal productoPorSucursal) {
        return new ProductoPorSucursalResponse(
                productoPorSucursal.sucursalId(),
                productoPorSucursal.sucursalNombre(),
                toResponse(productoPorSucursal.producto()));
    }

    private static SucursalResponse toResponse(Sucursal sucursal) {
        List<ProductoResponse> productos = sucursal.productos().stream()
                .map(FranquiciaDtoMapper::toResponse)
                .toList();
        return new SucursalResponse(sucursal.id(), sucursal.nombre(), productos);
    }

    private static ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(producto.id(), producto.nombre(), producto.stock());
    }
}
