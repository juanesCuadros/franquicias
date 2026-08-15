package com.mvc.franquicias.infrastructure.adapter.out.persistence;

import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.model.Producto;
import com.mvc.franquicias.domain.model.Sucursal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
class FranquiciaRepositoryAdapterIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private FranquiciaMongoRepository franquiciaMongoRepository;

    @Test
    void deberiaGuardarYRecuperarFranquiciaPorIdPreservandoSucursalesYProductos() {
        FranquiciaRepositoryAdapter adapter = new FranquiciaRepositoryAdapter(franquiciaMongoRepository);

        Producto producto = new Producto("p1", "Camisa", 10);
        Sucursal sucursal = new Sucursal("s1", "Centro", List.of(producto));
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", List.of(sucursal));

        StepVerifier.create(
                        adapter.guardar(franquicia)
                                .then(adapter.buscarPorId("f1")))
                .assertNext(recuperada -> {
                    assertThat(recuperada.id()).isEqualTo("f1");
                    assertThat(recuperada.nombre()).isEqualTo("Mi Franquicia");
                    assertThat(recuperada.sucursales()).hasSize(1);

                    Sucursal sucursalRecuperada = recuperada.sucursales().get(0);
                    assertThat(sucursalRecuperada.id()).isEqualTo("s1");
                    assertThat(sucursalRecuperada.nombre()).isEqualTo("Centro");
                    assertThat(sucursalRecuperada.productos()).hasSize(1);

                    Producto productoRecuperado = sucursalRecuperada.productos().get(0);
                    assertThat(productoRecuperado.id()).isEqualTo("p1");
                    assertThat(productoRecuperado.nombre()).isEqualTo("Camisa");
                    assertThat(productoRecuperado.stock()).isEqualTo(10);
                })
                .verifyComplete();
    }

    @Test
    void existePorIdDeberiaReflejarSiLaFranquiciaFueGuardada() {
        FranquiciaRepositoryAdapter adapter = new FranquiciaRepositoryAdapter(franquiciaMongoRepository);
        Franquicia franquicia = new Franquicia("f2", "Otra Franquicia", List.of());

        StepVerifier.create(
                        adapter.existePorId("f2")
                                .zipWith(adapter.guardar(franquicia)
                                        .then(adapter.existePorId("f2"))))
                .assertNext(tuple -> {
                    assertThat(tuple.getT1()).isFalse();
                    assertThat(tuple.getT2()).isTrue();
                })
                .verifyComplete();
    }
}
