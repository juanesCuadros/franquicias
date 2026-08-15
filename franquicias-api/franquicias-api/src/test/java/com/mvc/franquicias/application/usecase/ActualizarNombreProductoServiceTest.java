package com.mvc.franquicias.application.usecase;

import com.mvc.franquicias.domain.exception.FranquiciaNoEncontradaException;
import com.mvc.franquicias.domain.exception.ProductoNoEncontradoException;
import com.mvc.franquicias.domain.exception.SucursalNoEncontradaException;
import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.model.Producto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarNombreProductoServiceTest {

    @Mock
    private FranquiciaRepositoryPort franquiciaRepositoryPort;

    private ActualizarNombreProductoService service;

    @BeforeEach
    void setUp() {
        service = new ActualizarNombreProductoService(franquiciaRepositoryPort);
    }

    @Test
    void deberiaActualizarNombreDeProductoExistente() {
        Producto producto = new Producto("p1", "Nombre Viejo", 10);
        Sucursal sucursal = new Sucursal("s1", "Centro", List.of(producto));
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", List.of(sucursal));
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.just(franquicia));
        when(franquiciaRepositoryPort.guardar(any(Franquicia.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.actualizarNombre("f1", "s1", "p1", "Nombre Nuevo"))
                .assertNext(actualizada -> {
                    Producto productoActualizado = actualizada.sucursales().get(0).productos().get(0);
                    assertThat(productoActualizado.nombre()).isEqualTo("Nombre Nuevo");
                })
                .verifyComplete();
    }

    @Test
    void deberiaFallarSiLaFranquiciaNoExiste() {
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.empty());

        StepVerifier.create(service.actualizarNombre("f1", "s1", "p1", "Nombre Nuevo"))
                .expectError(FranquiciaNoEncontradaException.class)
                .verify();
    }

    @Test
    void deberiaFallarSiLaSucursalNoExiste() {
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", List.of());
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.just(franquicia));

        StepVerifier.create(service.actualizarNombre("f1", "s1", "p1", "Nombre Nuevo"))
                .expectError(SucursalNoEncontradaException.class)
                .verify();
    }

    @Test
    void deberiaFallarSiElProductoNoExiste() {
        Sucursal sucursal = new Sucursal("s1", "Centro", List.of());
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", List.of(sucursal));
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.just(franquicia));

        StepVerifier.create(service.actualizarNombre("f1", "s1", "p1", "Nombre Nuevo"))
                .expectError(ProductoNoEncontradoException.class)
                .verify();
    }
}
