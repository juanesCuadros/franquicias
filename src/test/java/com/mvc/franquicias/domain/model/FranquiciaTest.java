package com.mvc.franquicias.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FranquiciaTest {

    private final Sucursal sucursal1 = new Sucursal("s1", "Centro", List.of());
    private final Sucursal sucursal2 = new Sucursal("s2", "Norte", List.of());

    @Test
    void deberiaCrearFranquiciaValida() {
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", List.of(sucursal1));

        assertThat(franquicia.id()).isEqualTo("f1");
        assertThat(franquicia.nombre()).isEqualTo("Mi Franquicia");
        assertThat(franquicia.sucursales()).containsExactly(sucursal1);
    }

    @Test
    void deberiaCrearFranquiciaConListaVaciaSiSucursalesEsNulo() {
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", null);

        assertThat(franquicia.sucursales()).isEmpty();
    }

    @Test
    void deberiaFallarSiNombreEsVacio() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Franquicia("f1", "   ", List.of()));
    }

    @Test
    void deberiaFallarSiNombreEsNulo() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Franquicia("f1", null, List.of()));
    }

    @Test
    void listaDeSucursalesDeberiaSerInmutable() {
        Franquicia franquicia = new Franquicia("f1", "Mi Franquicia", new java.util.ArrayList<>(List.of(sucursal1)));

        assertThatThrownBy(() -> franquicia.sucursales().add(sucursal2))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void agregarSucursalDeberiaDevolverNuevaInstanciaSinMutarOriginal() {
        Franquicia original = new Franquicia("f1", "Mi Franquicia", List.of(sucursal1));

        Franquicia actualizada = original.agregarSucursal(sucursal2);

        assertThat(actualizada).isNotSameAs(original);
        assertThat(actualizada.sucursales()).containsExactly(sucursal1, sucursal2);
        assertThat(original.sucursales()).containsExactly(sucursal1);
    }

    @Test
    void actualizarNombreDeberiaDevolverNuevaInstanciaSinMutarOriginal() {
        Franquicia original = new Franquicia("f1", "Mi Franquicia", List.of(sucursal1));

        Franquicia actualizada = original.actualizarNombre("Otro Nombre");

        assertThat(actualizada).isNotSameAs(original);
        assertThat(actualizada.nombre()).isEqualTo("Otro Nombre");
        assertThat(original.nombre()).isEqualTo("Mi Franquicia");
    }
}
