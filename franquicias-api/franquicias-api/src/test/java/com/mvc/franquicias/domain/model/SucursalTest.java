package com.mvc.franquicias.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SucursalTest {

    private final Producto producto1 = new Producto("p1", "Camisa", 10);
    private final Producto producto2 = new Producto("p2", "Pantalón", 5);

    @Test
    void deberiaCrearSucursalValida() {
        Sucursal sucursal = new Sucursal("s1", "Centro", List.of(producto1));

        assertThat(sucursal.id()).isEqualTo("s1");
        assertThat(sucursal.nombre()).isEqualTo("Centro");
        assertThat(sucursal.productos()).containsExactly(producto1);
    }

    @Test
    void deberiaCrearSucursalConListaVaciaSiProductosEsNulo() {
        Sucursal sucursal = new Sucursal("s1", "Centro", null);

        assertThat(sucursal.productos()).isEmpty();
    }

    @Test
    void deberiaFallarSiNombreEsVacio() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Sucursal("s1", "", List.of()));
    }

    @Test
    void listaDeProductosDeberiaSerInmutable() {
        List<Producto> productosMutables = new java.util.ArrayList<>();
        productosMutables.add(producto1);
        Sucursal sucursal = new Sucursal("s1", "Centro", productosMutables);

        assertThatThrownBy(() -> sucursal.productos().add(producto2))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void agregarProductoDeberiaDevolverNuevaInstanciaSinMutarOriginal() {
        Sucursal original = new Sucursal("s1", "Centro", List.of(producto1));

        Sucursal actualizada = original.agregarProducto(producto2);

        assertThat(actualizada).isNotSameAs(original);
        assertThat(actualizada.productos()).containsExactly(producto1, producto2);
        assertThat(original.productos()).containsExactly(producto1);
    }

    @Test
    void eliminarProductoDeberiaDevolverNuevaInstanciaSinMutarOriginal() {
        Sucursal original = new Sucursal("s1", "Centro", List.of(producto1, producto2));

        Sucursal actualizada = original.eliminarProducto("p1");

        assertThat(actualizada).isNotSameAs(original);
        assertThat(actualizada.productos()).containsExactly(producto2);
        assertThat(original.productos()).containsExactly(producto1, producto2);
    }

    @Test
    void actualizarNombreDeberiaDevolverNuevaInstanciaSinMutarOriginal() {
        Sucursal original = new Sucursal("s1", "Centro", List.of(producto1));

        Sucursal actualizada = original.actualizarNombre("Norte");

        assertThat(actualizada).isNotSameAs(original);
        assertThat(actualizada.nombre()).isEqualTo("Norte");
        assertThat(original.nombre()).isEqualTo("Centro");
    }
}
