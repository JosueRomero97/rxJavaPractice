package com.mitocode.springreactore.controller;

import com.mitocode.springreactore.dto.InvoiceDto;
import com.mitocode.springreactore.model.Invoice;
import com.mitocode.springreactore.pagination.PageSupport;
import com.mitocode.springreactore.service.IInvoiceService;
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
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private  final IInvoiceService iInvoiceService;

    @Qualifier("invoiceMapper")
    private final ModelMapper modelMapper;

    @GetMapping
    public Mono<ResponseEntity<Flux<InvoiceDto>>> findAll(){

        Flux<InvoiceDto> fx = iInvoiceService.findAll()
                .map(this::convertToDto);
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                        .body(fx)
                );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<InvoiceDto>> findById(@PathVariable("id") String id){
        return iInvoiceService.findById(id).
                map(this::convertToDto).
                map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @GetMapping("/generateReport")
    public Mono<ResponseEntity<byte[]>> generateReport(@RequestParam("id") String id){
        return iInvoiceService.generateReport(id)
                .map(bytes-> ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_PDF)//APPLICATION_OCTET_STREAM APPLICATION_PDF
                        .body(bytes))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<InvoiceDto>> save(@Valid @RequestBody InvoiceDto invoiceDto, final ServerHttpRequest req){
        return iInvoiceService.save(this.convertToDocument(invoiceDto))
                .map(this::convertToDto)
                .map(e->ResponseEntity.created(
                                URI.create(req.getURI().toString().concat("/").concat(e.getId()))
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<InvoiceDto>> update(@PathVariable("id") String id, @RequestBody InvoiceDto invoiceDto){

       return Mono.just(convertToDocument(invoiceDto))
                        .map(e->{
                            e.setId(id);
                            return e;
                        })
               .flatMap(e-> iInvoiceService.update(id,e))
               .map(this::convertToDto)
               .map(e-> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
               .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id){

        return iInvoiceService.delete(id)
                .flatMap(result ->{
                    if (result){ return Mono.just(ResponseEntity.noContent().build());}
                    else{ return Mono.just(ResponseEntity.notFound().build());}
                });
    }

    @GetMapping("pageable")
    public Mono<ResponseEntity<PageSupport<InvoiceDto>>> getPage(
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "size",defaultValue = "2") int size
    ){
        return iInvoiceService.obtenerPagina(PageRequest.of(page,size))
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

    private InvoiceDto invoiceHateoas;

    @GetMapping("/hateoas/v1/{id}")
    private Mono<EntityModel<InvoiceDto>> getHateOasV1(@PathVariable("id") String id){
        Mono<Link> monoLink = WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(InvoiceController.class).findById(id)).withRel("Invoice-info").toMono();
        //practica comun y no recomendad
        return iInvoiceService.findById(id)
                .map(this::convertToDto)
                .flatMap(d->{
                    this.invoiceHateoas=d;
                    return monoLink;
                })
                .map(link -> EntityModel.of(this.invoiceHateoas,link));
    }

    @GetMapping("/hateoas/v2/{id}")
    private Mono<EntityModel<InvoiceDto>> getHateOasV2(@PathVariable("id") String id){
        Mono<Link> monoLink = WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(InvoiceController.class).findById(id)).withRel("Invoice-info-2").toMono();
        return iInvoiceService.findById(id)
                .map(this::convertToDto)
                .flatMap(d-> monoLink.map(link -> EntityModel.of(d,link)));
    }

    @GetMapping("/hateoas/v3/{id}")
    private Mono<EntityModel<InvoiceDto>> getHateOasV3(@PathVariable("id") String id){
        Mono<Link> monoLink = WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(InvoiceController.class).findById(id)).withRel("Invoice-info-2").toMono();

        return iInvoiceService.findById(id)
                .map(this::convertToDto)
                .zipWith(monoLink,(d,link)-> EntityModel.of(d,link));
    }


    private InvoiceDto convertToDto(Invoice model){
        return modelMapper.map(model,InvoiceDto.class);
    }
    private Invoice convertToDocument(InvoiceDto dto){
        return modelMapper.map(dto,Invoice.class);
    }
}
