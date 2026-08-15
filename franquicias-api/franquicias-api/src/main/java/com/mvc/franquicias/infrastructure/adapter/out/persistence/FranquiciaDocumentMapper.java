package com.mvc.franquicias.infrastructure.adapter.out.persistence;

import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.model.Producto;
import com.mvc.franquicias.domain.model.Sucursal;

import java.util.List;

public final class FranquiciaDocumentMapper {

    private FranquiciaDocumentMapper() {
    }

    public static Franquicia toDomain(FranquiciaDocument documento) {
        List<Sucursal> sucursales = documento.getSucursales() == null
                ? List.of()
                : documento.getSucursales().stream().map(FranquiciaDocumentMapper::toDomain).toList();
        return new Franquicia(documento.getId(), documento.getNombre(), sucursales);
    }

    public static FranquiciaDocument toDocument(Franquicia franquicia) {
        List<SucursalDocument> sucursales = franquicia.sucursales().stream()
                .map(FranquiciaDocumentMapper::toDocument)
                .toList();
        return FranquiciaDocument.builder()
                .id(franquicia.id())
                .nombre(franquicia.nombre())
                .sucursales(sucursales)
                .build();
    }

    private static Sucursal toDomain(SucursalDocument documento) {
        List<Producto> productos = documento.getProductos() == null
                ? List.of()
                : documento.getProductos().stream().map(FranquiciaDocumentMapper::toDomain).toList();
        return new Sucursal(documento.getId(), documento.getNombre(), productos);
    }

    private static SucursalDocument toDocument(Sucursal sucursal) {
        List<ProductoDocument> productos = sucursal.productos().stream()
                .map(FranquiciaDocumentMapper::toDocument)
                .toList();
        return SucursalDocument.builder()
                .id(sucursal.id())
                .nombre(sucursal.nombre())
                .productos(productos)
                .build();
    }

    private static Producto toDomain(ProductoDocument documento) {
        return new Producto(documento.getId(), documento.getNombre(), documento.getStock());
    }

    private static ProductoDocument toDocument(Producto producto) {
        return ProductoDocument.builder()
                .id(producto.id())
                .nombre(producto.nombre())
                .stock(producto.stock())
                .build();
    }
}
