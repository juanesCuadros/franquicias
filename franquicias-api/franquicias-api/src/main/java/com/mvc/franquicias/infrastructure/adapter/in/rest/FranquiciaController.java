package com.mvc.franquicias.infrastructure.adapter.in.rest;

import com.mvc.franquicias.domain.port.in.ActualizarNombreFranquiciaUseCase;
import com.mvc.franquicias.domain.port.in.ActualizarNombreProductoUseCase;
import com.mvc.franquicias.domain.port.in.ActualizarNombreSucursalUseCase;
import com.mvc.franquicias.domain.port.in.ActualizarStockUseCase;
import com.mvc.franquicias.domain.port.in.AgregarProductoUseCase;
import com.mvc.franquicias.domain.port.in.AgregarSucursalUseCase;
import com.mvc.franquicias.domain.port.in.CrearFranquiciaUseCase;
import com.mvc.franquicias.domain.port.in.EliminarProductoUseCase;
import com.mvc.franquicias.domain.port.in.ObtenerProductoConMasStockPorSucursalUseCase;
import com.mvc.franquicias.infrastructure.adapter.in.rest.dto.ActualizarNombreRequest;
import com.mvc.franquicias.infrastructure.adapter.in.rest.dto.ActualizarStockRequest;
import com.mvc.franquicias.infrastructure.adapter.in.rest.dto.AgregarProductoRequest;
import com.mvc.franquicias.infrastructure.adapter.in.rest.dto.AgregarSucursalRequest;
import com.mvc.franquicias.infrastructure.adapter.in.rest.dto.CrearFranquiciaRequest;
import com.mvc.franquicias.infrastructure.adapter.in.rest.dto.FranquiciaDtoMapper;
import com.mvc.franquicias.infrastructure.adapter.in.rest.dto.FranquiciaResponse;
import com.mvc.franquicias.infrastructure.adapter.in.rest.dto.ProductoPorSucursalResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/franquicias")
public class FranquiciaController {

    private final CrearFranquiciaUseCase crearFranquiciaUseCase;
    private final AgregarSucursalUseCase agregarSucursalUseCase;
    private final AgregarProductoUseCase agregarProductoUseCase;
    private final EliminarProductoUseCase eliminarProductoUseCase;
    private final ActualizarStockUseCase actualizarStockUseCase;
    private final ActualizarNombreFranquiciaUseCase actualizarNombreFranquiciaUseCase;
    private final ActualizarNombreSucursalUseCase actualizarNombreSucursalUseCase;
    private final ActualizarNombreProductoUseCase actualizarNombreProductoUseCase;
    private final ObtenerProductoConMasStockPorSucursalUseCase obtenerProductoConMasStockPorSucursalUseCase;

    public FranquiciaController(
            CrearFranquiciaUseCase crearFranquiciaUseCase,
            AgregarSucursalUseCase agregarSucursalUseCase,
            AgregarProductoUseCase agregarProductoUseCase,
            EliminarProductoUseCase eliminarProductoUseCase,
            ActualizarStockUseCase actualizarStockUseCase,
            ActualizarNombreFranquiciaUseCase actualizarNombreFranquiciaUseCase,
            ActualizarNombreSucursalUseCase actualizarNombreSucursalUseCase,
            ActualizarNombreProductoUseCase actualizarNombreProductoUseCase,
            ObtenerProductoConMasStockPorSucursalUseCase obtenerProductoConMasStockPorSucursalUseCase) {
        this.crearFranquiciaUseCase = crearFranquiciaUseCase;
        this.agregarSucursalUseCase = agregarSucursalUseCase;
        this.agregarProductoUseCase = agregarProductoUseCase;
        this.eliminarProductoUseCase = eliminarProductoUseCase;
        this.actualizarStockUseCase = actualizarStockUseCase;
        this.actualizarNombreFranquiciaUseCase = actualizarNombreFranquiciaUseCase;
        this.actualizarNombreSucursalUseCase = actualizarNombreSucursalUseCase;
        this.actualizarNombreProductoUseCase = actualizarNombreProductoUseCase;
        this.obtenerProductoConMasStockPorSucursalUseCase = obtenerProductoConMasStockPorSucursalUseCase;
    }

    @PostMapping
    public Mono<ResponseEntity<FranquiciaResponse>> crearFranquicia(@Valid @RequestBody CrearFranquiciaRequest request) {
        return crearFranquiciaUseCase.crearFranquicia(request.nombre())
                .map(FranquiciaDtoMapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PostMapping("/{franquiciaId}/sucursales")
    public Mono<ResponseEntity<FranquiciaResponse>> agregarSucursal(
            @PathVariable String franquiciaId,
            @Valid @RequestBody AgregarSucursalRequest request) {
        return agregarSucursalUseCase.agregarSucursal(franquiciaId, request.nombre())
                .map(FranquiciaDtoMapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PostMapping("/{franquiciaId}/sucursales/{sucursalId}/productos")
    public Mono<ResponseEntity<FranquiciaResponse>> agregarProducto(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @Valid @RequestBody AgregarProductoRequest request) {
        return agregarProductoUseCase.agregarProducto(franquiciaId, sucursalId, request.nombre(), request.stock())
                .map(FranquiciaDtoMapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @DeleteMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}")
    public Mono<ResponseEntity<Void>> eliminarProducto(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @PathVariable String productoId) {
        return eliminarProductoUseCase.eliminarProducto(franquiciaId, sucursalId, productoId)
                .map(franquicia -> ResponseEntity.noContent().<Void>build());
    }

    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock")
    public Mono<ResponseEntity<FranquiciaResponse>> actualizarStock(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @PathVariable String productoId,
            @Valid @RequestBody ActualizarStockRequest request) {
        return actualizarStockUseCase.actualizarStock(franquiciaId, sucursalId, productoId, request.stock())
                .map(FranquiciaDtoMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{franquiciaId}/productos/mas-stock")
    public Flux<ProductoPorSucursalResponse> obtenerProductoConMasStockPorSucursal(@PathVariable String franquiciaId) {
        return obtenerProductoConMasStockPorSucursalUseCase.obtenerProductoConMasStockPorSucursal(franquiciaId)
                .map(FranquiciaDtoMapper::toResponse);
    }

    @PatchMapping("/{franquiciaId}")
    public Mono<ResponseEntity<FranquiciaResponse>> actualizarNombreFranquicia(
            @PathVariable String franquiciaId,
            @Valid @RequestBody ActualizarNombreRequest request) {
        return actualizarNombreFranquiciaUseCase.actualizarNombre(franquiciaId, request.nombre())
                .map(FranquiciaDtoMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}")
    public Mono<ResponseEntity<FranquiciaResponse>> actualizarNombreSucursal(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @Valid @RequestBody ActualizarNombreRequest request) {
        return actualizarNombreSucursalUseCase.actualizarNombre(franquiciaId, sucursalId, request.nombre())
                .map(FranquiciaDtoMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}")
    public Mono<ResponseEntity<FranquiciaResponse>> actualizarNombreProducto(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @PathVariable String productoId,
            @Valid @RequestBody ActualizarNombreRequest request) {
        return actualizarNombreProductoUseCase.actualizarNombre(franquiciaId, sucursalId, productoId, request.nombre())
                .map(FranquiciaDtoMapper::toResponse)
                .map(ResponseEntity::ok);
    }
}
