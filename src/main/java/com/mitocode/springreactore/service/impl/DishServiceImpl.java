package com.mitocode.springreactore.service.impl;

import com.mitocode.springreactore.model.Dish;
import com.mitocode.springreactore.repo.IDishRepo;
import com.mitocode.springreactore.repo.IGenericRepo;
import com.mitocode.springreactore.service.IDishService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DishServiceImpl extends CRUDImpl<Dish,String> implements IDishService {


    private final IDishRepo repo;

    @Override
    protected IGenericRepo<Dish, String> getRepo() {
        return repo;
    }

}
