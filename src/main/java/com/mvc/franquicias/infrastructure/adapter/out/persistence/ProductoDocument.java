package com.mvc.franquicias.infrastructure.adapter.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDocument {

    @Field("id")
    private String id;

    @Field("nombre")
    private String nombre;

    @Field("stock")
    private int stock;
}
