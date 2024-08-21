package com.mitocode.springreactore.controller;

import com.mitocode.springreactore.config.ConfigMapper;
import com.mitocode.springreactore.dto.DishDto;
import com.mitocode.springreactore.model.Dish;
import com.mitocode.springreactore.service.IDishService;
import jakarta.validation.Valid;
import jdk.jfr.ContentType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {
    private  final IDishService iDishService;
    private final ModelMapper modelMapper;

    @GetMapping
    public Mono<ResponseEntity<Flux<DishDto>>> findAll(){

        Flux<DishDto> fx = iDishService.findAll()
                .map(this::convertToDto);
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                        .body(fx)
                );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<DishDto>> findById(@PathVariable("id") String id){
        return iDishService.findById(id).
                map(this::convertToDto).
                map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    

    @PostMapping
    public Mono<ResponseEntity<DishDto>> save(@Valid @RequestBody DishDto dishDto, final ServerHttpRequest req){
        return iDishService.save(this.convertToDocument(dishDto))
                .map(this::convertToDto)
                .map(e->ResponseEntity.created(
                                URI.create(req.getURI().toString().concat("/").concat(e.getId()))
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<DishDto>> update(@PathVariable("id") String id, @RequestBody DishDto dishDto){

       return Mono.just(convertToDocument(dishDto))
                        .map(e->{
                            e.setId(id);
                            return e;
                        })
               .flatMap(e-> iDishService.update(id,e))
               .map(this::convertToDto)
               .map(e-> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
               .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id){

        return iDishService.delete(id)
                .flatMap(result ->{
                    if (result){ return Mono.just(ResponseEntity.noContent().build());}
                    else{ return Mono.just(ResponseEntity.notFound().build());}
                });
    }

    private DishDto convertToDto(Dish model){
        return modelMapper.map(model,DishDto.class);
    }
    private Dish convertToDocument(DishDto dto){
        return modelMapper.map(dto,Dish.class);
    }
}
