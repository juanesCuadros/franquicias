package com.mvc.franquicias.application.usecase;

import com.mvc.franquicias.domain.exception.FranquiciaNoEncontradaException;
import com.mvc.franquicias.domain.model.Franquicia;
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
class AgregarSucursalServiceTest {

    @Mock
    private FranquiciaRepositoryPort franquiciaRepositoryPort;

    private AgregarSucursalService service;

    @BeforeEach
    void setUp() {
        service = new AgregarSucursalService(franquiciaRepositoryPort);
    }

    @Test
    void deberiaAgregarSucursalAFranquiciaExistente() {
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", List.of());
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.just(franquicia));
        when(franquiciaRepositoryPort.guardar(any(Franquicia.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.agregarSucursal("f1", "Centro"))
                .assertNext(actualizada -> {
                    assertThat(actualizada.sucursales()).hasSize(1);
                    assertThat(actualizada.sucursales().get(0).nombre()).isEqualTo("Centro");
                    assertThat(actualizada.sucursales().get(0).id()).isNotBlank();
                })
                .verifyComplete();
    }

    @Test
    void deberiaFallarSiLaFranquiciaNoExiste() {
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.empty());

        StepVerifier.create(service.agregarSucursal("f1", "Centro"))
                .expectError(FranquiciaNoEncontradaException.class)
                .verify();
    }
}
