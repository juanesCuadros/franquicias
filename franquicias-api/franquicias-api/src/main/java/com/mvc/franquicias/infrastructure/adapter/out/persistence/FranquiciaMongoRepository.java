package com.mvc.franquicias.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FranquiciaMongoRepository extends ReactiveMongoRepository<FranquiciaDocument, String> {
}
