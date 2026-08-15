package com.mvc.franquicias.application.usecase;

import com.mvc.franquicias.domain.exception.FranquiciaNoEncontradaException;
import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.model.Producto;
import com.mvc.franquicias.domain.model.ProductoPorSucursal;
import com.mvc.franquicias.domain.model.Sucursal;
import com.mvc.franquicias.domain.port.out.FranquiciaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerProductoConMasStockPorSucursalServiceTest {

    @Mock
    private FranquiciaRepositoryPort franquiciaRepositoryPort;

    private ObtenerProductoConMasStockPorSucursalService service;

    @BeforeEach
    void setUp() {
        service = new ObtenerProductoConMasStockPorSucursalService(franquiciaRepositoryPort);
    }

    @Test
    void deberiaDevolverElProductoConMasStockPorCadaSucursal() {
        Sucursal sucursal1 = new Sucursal("s1", "Centro", List.of(
                new Producto("p1", "Camisa", 5),
                new Producto("p2", "Pantalón", 20)));
        Sucursal sucursal2 = new Sucursal("s2", "Norte", List.of(
                new Producto("p3", "Zapatos", 30),
                new Producto("p4", "Medias", 8)));
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", List.of(sucursal1, sucursal2));
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.just(franquicia));

        StepVerifier.create(service.obtenerProductoConMasStockPorSucursal("f1"))
                .expectNext(new ProductoPorSucursal("s1", "Centro", new Producto("p2", "Pantalón", 20)))
                .expectNext(new ProductoPorSucursal("s2", "Norte", new Producto("p3", "Zapatos", 30)))
                .verifyComplete();
    }

    @Test
    void deberiaExcluirSucursalesSinProductos() {
        Sucursal sucursalSinProductos = new Sucursal("s1", "Centro", List.of());
        Sucursal sucursalConProductos = new Sucursal("s2", "Norte", List.of(new Producto("p1", "Zapatos", 30)));
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", List.of(sucursalSinProductos, sucursalConProductos));
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.just(franquicia));

        StepVerifier.create(service.obtenerProductoConMasStockPorSucursal("f1"))
                .expectNext(new ProductoPorSucursal("s2", "Norte", new Producto("p1", "Zapatos", 30)))
                .verifyComplete();
    }

    // Criterio de desempate documentado en ObtenerProductoConMasStockPorSucursalService: ante
    // stock igual, se conserva el PRIMER producto en el orden de la lista de la sucursal.
    @Test
    void enCasoDeEmpateDeberiaConservarElPrimerProductoEnOrdenDeLista() {
        Producto primero = new Producto("p1", "Camisa", 10);
        Producto segundo = new Producto("p2", "Pantalón", 10);
        Sucursal sucursal = new Sucursal("s1", "Centro", List.of(primero, segundo));
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", List.of(sucursal));
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.just(franquicia));

        StepVerifier.create(service.obtenerProductoConMasStockPorSucursal("f1"))
                .expectNext(new ProductoPorSucursal("s1", "Centro", primero))
                .verifyComplete();
    }

    @Test
    void deberiaFallarSiLaFranquiciaNoExiste() {
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.empty());

        StepVerifier.create(service.obtenerProductoConMasStockPorSucursal("f1"))
                .expectError(FranquiciaNoEncontradaException.class)
                .verify();
    }
}
