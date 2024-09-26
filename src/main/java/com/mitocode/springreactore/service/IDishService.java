package com.mitocode.springreactore.service;

import com.mitocode.springreactore.model.Dish;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface IDishService extends IGenericService<Dish,String> {



}
