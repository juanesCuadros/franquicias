package com.mvc.franquicias.infrastructure.adapter.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "franquicias")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranquiciaDocument {

    @Id
    private String id;

    @Field("nombre")
    private String nombre;

    @Field("sucursales")
    private List<SucursalDocument> sucursales;
}
