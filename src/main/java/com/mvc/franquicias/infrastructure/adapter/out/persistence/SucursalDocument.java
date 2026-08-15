package com.mvc.franquicias.infrastructure.adapter.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SucursalDocument {

    @Field("id")
    private String id;

    @Field("nombre")
    private String nombre;

    @Field("productos")
    private List<ProductoDocument> productos;
}
