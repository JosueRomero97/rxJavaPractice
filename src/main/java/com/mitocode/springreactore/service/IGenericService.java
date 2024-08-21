package com.mitocode.springreactore.service;

import com.mitocode.springreactore.pagination.PageSupport;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGenericService<T,ID>{
    Mono<T> save(T t);
    Mono<T> update(ID id,T t);
    Flux<T> findAll();
    Mono<T> findById(ID id);
    Mono<Boolean> delete(ID id);

    Mono<PageSupport<T>> obtenerPagina(Pageable pageable);
}
