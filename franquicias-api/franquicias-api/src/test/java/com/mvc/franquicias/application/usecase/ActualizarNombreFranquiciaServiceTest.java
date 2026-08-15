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
class ActualizarNombreFranquiciaServiceTest {

    @Mock
    private FranquiciaRepositoryPort franquiciaRepositoryPort;

    private ActualizarNombreFranquiciaService service;

    @BeforeEach
    void setUp() {
        service = new ActualizarNombreFranquiciaService(franquiciaRepositoryPort);
    }

    @Test
    void deberiaActualizarNombreDeFranquiciaExistente() {
        Franquicia franquicia = new Franquicia("f1", "Nombre Viejo", List.of());
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.just(franquicia));
        when(franquiciaRepositoryPort.guardar(any(Franquicia.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.actualizarNombre("f1", "Nombre Nuevo"))
                .assertNext(actualizada -> assertThat(actualizada.nombre()).isEqualTo("Nombre Nuevo"))
                .verifyComplete();
    }

    @Test
    void deberiaFallarSiLaFranquiciaNoExiste() {
        when(franquiciaRepositoryPort.buscarPorId("f1")).thenReturn(Mono.empty());

        StepVerifier.create(service.actualizarNombre("f1", "Nombre Nuevo"))
                .expectError(FranquiciaNoEncontradaException.class)
                .verify();
    }
}
