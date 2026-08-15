package com.mvc.franquicias.infrastructure.adapter.in.rest;

import com.mvc.franquicias.domain.exception.FranquiciaNoEncontradaException;
import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.model.Producto;
import com.mvc.franquicias.domain.model.ProductoPorSucursal;
import com.mvc.franquicias.domain.model.Sucursal;
import com.mvc.franquicias.domain.port.in.ActualizarNombreFranquiciaUseCase;
import com.mvc.franquicias.domain.port.in.ActualizarNombreProductoUseCase;
import com.mvc.franquicias.domain.port.in.ActualizarNombreSucursalUseCase;
import com.mvc.franquicias.domain.port.in.ActualizarStockUseCase;
import com.mvc.franquicias.domain.port.in.AgregarProductoUseCase;
import com.mvc.franquicias.domain.port.in.AgregarSucursalUseCase;
import com.mvc.franquicias.domain.port.in.CrearFranquiciaUseCase;
import com.mvc.franquicias.domain.port.in.EliminarProductoUseCase;
import com.mvc.franquicias.domain.port.in.ObtenerProductoConMasStockPorSucursalUseCase;
import com.mvc.franquicias.infrastructure.adapter.in.rest.dto.AgregarProductoRequest;
import com.mvc.franquicias.infrastructure.adapter.in.rest.dto.AgregarSucursalRequest;
import com.mvc.franquicias.infrastructure.adapter.in.rest.dto.CrearFranquiciaRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(FranquiciaController.class)
class FranquiciaControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CrearFranquiciaUseCase crearFranquiciaUseCase;

    @MockitoBean
    private AgregarSucursalUseCase agregarSucursalUseCase;

    @MockitoBean
    private AgregarProductoUseCase agregarProductoUseCase;

    @MockitoBean
    private EliminarProductoUseCase eliminarProductoUseCase;

    @MockitoBean
    private ActualizarStockUseCase actualizarStockUseCase;

    @MockitoBean
    private ActualizarNombreFranquiciaUseCase actualizarNombreFranquiciaUseCase;

    @MockitoBean
    private ActualizarNombreSucursalUseCase actualizarNombreSucursalUseCase;

    @MockitoBean
    private ActualizarNombreProductoUseCase actualizarNombreProductoUseCase;

    @MockitoBean
    private ObtenerProductoConMasStockPorSucursalUseCase obtenerProductoConMasStockPorSucursalUseCase;

    @Test
    void deberiaCrearFranquiciaYDevolver201() {
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", List.of());
        when(crearFranquiciaUseCase.crearFranquicia("Mi Franquicia")).thenReturn(Mono.just(franquicia));

        webTestClient.post().uri("/api/franquicias")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CrearFranquiciaRequest("Mi Franquicia"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("f1")
                .jsonPath("$.nombre").isEqualTo("Mi Franquicia")
                .jsonPath("$.sucursales").isArray();
    }

    @Test
    void deberiaAgregarProductoYDevolver201() {
        Producto producto = new Producto("p1", "Camisa", 10);
        Sucursal sucursal = new Sucursal("s1", "Centro", List.of(producto));
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", List.of(sucursal));
        when(agregarProductoUseCase.agregarProducto("f1", "s1", "Camisa", 10)).thenReturn(Mono.just(franquicia));

        webTestClient.post().uri("/api/franquicias/f1/sucursales/s1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AgregarProductoRequest("Camisa", 10))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.sucursales[0].productos[0].nombre").isEqualTo("Camisa")
                .jsonPath("$.sucursales[0].productos[0].stock").isEqualTo(10);
    }

    @Test
    void deberiaDevolverProductoConMasStockPorSucursal() {
        ProductoPorSucursal productoPorSucursal1 = new ProductoPorSucursal("s1", "Centro", new Producto("p1", "Camisa", 10));
        ProductoPorSucursal productoPorSucursal2 = new ProductoPorSucursal("s2", "Norte", new Producto("p2", "Zapatos", 30));
        when(obtenerProductoConMasStockPorSucursalUseCase.obtenerProductoConMasStockPorSucursal("f1"))
                .thenReturn(Flux.just(productoPorSucursal1, productoPorSucursal2));

        webTestClient.get().uri("/api/franquicias/f1/productos/mas-stock")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(2);
    }

    @Test
    void deberiaDevolver404SiLaFranquiciaNoExisteAlAgregarSucursal() {
        when(agregarSucursalUseCase.agregarSucursal(eq("f1"), any()))
                .thenReturn(Mono.error(new FranquiciaNoEncontradaException("f1")));

        webTestClient.post().uri("/api/franquicias/f1/sucursales")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AgregarSucursalRequest("Centro"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("NO_ENCONTRADO")
                .jsonPath("$.mensaje").isEqualTo("Franquicia con id f1 no encontrada");
    }
}
