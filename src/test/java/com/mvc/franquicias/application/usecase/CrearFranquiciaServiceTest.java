package com.mvc.franquicias.application.usecase;

import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.port.out.FranquiciaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearFranquiciaServiceTest {

    @Mock
    private FranquiciaRepositoryPort franquiciaRepositoryPort;

    private CrearFranquiciaService service;

    @BeforeEach
    void setUp() {
        service = new CrearFranquiciaService(franquiciaRepositoryPort);
    }

    @Test
    void deberiaCrearFranquiciaConIdGeneradoYListaDeSucursalesVacia() {
        when(franquiciaRepositoryPort.guardar(any(Franquicia.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.crearFranquicia("Mi Franquicia"))
                .assertNext(franquicia -> {
                    assertThat(franquicia.id()).isNotBlank();
                    assertThat(franquicia.nombre()).isEqualTo("Mi Franquicia");
                    assertThat(franquicia.sucursales()).isEmpty();
                })
                .verifyComplete();

        ArgumentCaptor<Franquicia> captor = ArgumentCaptor.forClass(Franquicia.class);
        verify(franquiciaRepositoryPort).guardar(captor.capture());
        assertThat(captor.getValue().nombre()).isEqualTo("Mi Franquicia");
    }
}
