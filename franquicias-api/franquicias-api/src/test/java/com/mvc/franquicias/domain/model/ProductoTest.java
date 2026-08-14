package com.mvc.franquicias.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class ProductoTest {

    @Test
    void deberiaCrearProductoValido() {
        Producto producto = new Producto("p1", "Camisa", 10);

        assertThat(producto.id()).isEqualTo("p1");
        assertThat(producto.nombre()).isEqualTo("Camisa");
        assertThat(producto.stock()).isEqualTo(10);
    }

    @Test
    void deberiaFallarSiNombreEsVacio() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Producto("p1", "  ", 10));
    }

    @Test
    void deberiaFallarSiNombreEsNulo() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Producto("p1", null, 10));
    }

    @Test
    void deberiaFallarSiStockEsNegativo() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Producto("p1", "Camisa", -1));
    }

    @Test
    void actualizarStockDeberiaDevolverNuevaInstanciaSinMutarOriginal() {
        Producto original = new Producto("p1", "Camisa", 10);

        Producto actualizado = original.actualizarStock(25);

        assertThat(actualizado).isNotSameAs(original);
        assertThat(actualizado.stock()).isEqualTo(25);
        assertThat(original.stock()).isEqualTo(10);
    }

    @Test
    void actualizarNombreDeberiaDevolverNuevaInstanciaSinMutarOriginal() {
        Producto original = new Producto("p1", "Camisa", 10);

        Producto actualizado = original.actualizarNombre("Pantalón");

        assertThat(actualizado).isNotSameAs(original);
        assertThat(actualizado.nombre()).isEqualTo("Pantalón");
        assertThat(original.nombre()).isEqualTo("Camisa");
    }
}
