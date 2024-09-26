package com.mitocode.springreactore.controller;

import com.mitocode.springreactore.dto.ClientDto;
import com.mitocode.springreactore.dto.ClientDto;
import com.mitocode.springreactore.model.Client;
import com.mitocode.springreactore.pagination.PageSupport;
import com.mitocode.springreactore.service.IClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private  final IClientService iClientService;

    @Qualifier("clientMapper")
    private final ModelMapper modelMapper;

    @GetMapping
    public Mono<ResponseEntity<Flux<ClientDto>>> findAll(){

        Flux<ClientDto> fx = iClientService.findAll()
                .map(this::convertToDto);
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                        .body(fx)
                );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ClientDto>> findById(@PathVariable("id") String id){
        return iClientService.findById(id).
                map(this::convertToDto).
                map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    

    @PostMapping
    public Mono<ResponseEntity<ClientDto>> save(@Valid @RequestBody ClientDto clientDto, final ServerHttpRequest req){
        return iClientService.save(this.convertToDocument(clientDto))
                .map(this::convertToDto)
                .map(e->ResponseEntity.created(
                                URI.create(req.getURI().toString().concat("/").concat(e.getId()))
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ClientDto>> update(@PathVariable("id") String id, @RequestBody ClientDto clientDto){

       return Mono.just(convertToDocument(clientDto))
                        .map(e->{
                            e.setId(id);
                            return e;
                        })
               .flatMap(e-> iClientService.update(id,e))
               .map(this::convertToDto)
               .map(e-> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
               .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id){

        return iClientService.delete(id)
                .flatMap(result ->{
                    if (result){ return Mono.just(ResponseEntity.noContent().build());}
                    else{ return Mono.just(ResponseEntity.notFound().build());}
                });
    }

    @GetMapping("pageable")
    public Mono<ResponseEntity<PageSupport<ClientDto>>> getPage(
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "size",defaultValue = "2") int size
    ){
        return iClientService.obtenerPagina(PageRequest.of(page,size))
                .map(pageSupport -> new PageSupport<>(
                        pageSupport.getContent().stream().map(this::convertToDto).toList(),
                        pageSupport.getPageNumber(),
                        pageSupport.getPageSize(),
                        pageSupport.getTotalElements()
                ))
                .map(e-> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private ClientDto clientHateoas;

    @GetMapping("/hateoas/v1/{id}")
    private Mono<EntityModel<ClientDto>> getHateOasV1(@PathVariable("id") String id){
        Mono<Link> monoLink = WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(ClientController.class).findById(id)).withRel("Client-info").toMono();
        //practica comun y no recomendad
        return iClientService.findById(id)
                .map(this::convertToDto)
                .flatMap(d->{
                    this.clientHateoas=d;
                    return monoLink;
                })
                .map(link -> EntityModel.of(this.clientHateoas,link));
    }

    @GetMapping("/hateoas/v2/{id}")
    private Mono<EntityModel<ClientDto>> getHateOasV2(@PathVariable("id") String id){
        Mono<Link> monoLink = WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(ClientController.class).findById(id)).withRel("Client-info-2").toMono();
        return iClientService.findById(id)
                .map(this::convertToDto)
                .flatMap(d-> monoLink.map(link -> EntityModel.of(d,link)));
    }

    @GetMapping("/hateoas/v3/{id}")
    private Mono<EntityModel<ClientDto>> getHateOasV3(@PathVariable("id") String id){
        Mono<Link> monoLink = WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(ClientController.class).findById(id)).withRel("Client-info-2").toMono();

        return iClientService.findById(id)
                .map(this::convertToDto)
                .zipWith(monoLink,(d,link)-> EntityModel.of(d,link));
    }


    private ClientDto convertToDto(Client model){
        return modelMapper.map(model,ClientDto.class);
    }
    private Client convertToDocument(ClientDto dto){
        return modelMapper.map(dto,Client.class);
    }
}
