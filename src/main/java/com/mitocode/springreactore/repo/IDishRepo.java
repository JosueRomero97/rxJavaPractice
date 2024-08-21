package com.mitocode.springreactore.repo;

import com.mitocode.springreactore.model.Dish;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IDishRepo extends IGenericRepo<Dish,String> {

}
