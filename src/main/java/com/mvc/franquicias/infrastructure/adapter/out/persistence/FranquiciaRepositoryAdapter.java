package com.mvc.franquicias.infrastructure.adapter.out.persistence;

import com.mvc.franquicias.domain.model.Franquicia;
import com.mvc.franquicias.domain.port.out.FranquiciaRepositoryPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FranquiciaRepositoryAdapter implements FranquiciaRepositoryPort {

    private final FranquiciaMongoRepository franquiciaMongoRepository;

    public FranquiciaRepositoryAdapter(FranquiciaMongoRepository franquiciaMongoRepository) {
        this.franquiciaMongoRepository = franquiciaMongoRepository;
    }

    @Override
    public Mono<Franquicia> guardar(Franquicia franquicia) {
        return franquiciaMongoRepository.save(FranquiciaDocumentMapper.toDocument(franquicia))
                .map(FranquiciaDocumentMapper::toDomain);
    }

    @Override
    public Mono<Franquicia> buscarPorId(String id) {
        return franquiciaMongoRepository.findById(id)
                .map(FranquiciaDocumentMapper::toDomain);
    }

    @Override
    public Flux<Franquicia> buscarTodas() {
        return franquiciaMongoRepository.findAll()
                .map(FranquiciaDocumentMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existePorId(String id) {
        return franquiciaMongoRepository.existsById(id);
    }
}
