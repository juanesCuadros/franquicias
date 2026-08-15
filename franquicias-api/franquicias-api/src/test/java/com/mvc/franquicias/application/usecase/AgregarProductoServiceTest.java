package com.mvc.franquicias.application.usecase;

import com.mvc.franquicias.domain.exception.FranquiciaNoEncontradaException;
import com.mvc.franquicias.domain.exception.SucursalNoEncontradaException;
import com.mvc.franquicias.domain.model.Franquicia;
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
class AgregarProductoServiceTest {

    @Mock
    private FranquiciaRepositoryPort franquiciaRepositoryPort;

    private AgregarProductoService service;

    @BeforeEach
    void setUp() {
        service = new AgregarProductoService(franquiciaRepositoryPort);
    }

    @Test
    void deberiaAgregarProductoASucursalExistente() {
        Sucursal sucursal = new Sucursal("s1", "Centro", List.of());
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", List.of(sucursal));
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.just(franquicia));
        when(franquiciaRepositoryPort.guardar(any(Franquicia.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.agregarProducto("f1", "s1", "Camisa", 10))
                .assertNext(actualizada -> {
                    Sucursal sucursalActualizada = actualizada.sucursales().get(0);
                    assertThat(sucursalActualizada.productos()).hasSize(1);
                    assertThat(sucursalActualizada.productos().get(0).nombre()).isEqualTo("Camisa");
                    assertThat(sucursalActualizada.productos().get(0).stock()).isEqualTo(10);
                })
                .verifyComplete();
    }

    @Test
    void deberiaFallarSiLaFranquiciaNoExiste() {
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.empty());

        StepVerifier.create(service.agregarProducto("f1", "s1", "Camisa", 10))
                .expectError(FranquiciaNoEncontradaException.class)
                .verify();
    }

    @Test
    void deberiaFallarSiLaSucursalNoExiste() {
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", List.of());
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.just(franquicia));

        StepVerifier.create(service.agregarProducto("f1", "s1", "Camisa", 10))
                .expectError(SucursalNoEncontradaException.class)
                .verify();
    }
}
