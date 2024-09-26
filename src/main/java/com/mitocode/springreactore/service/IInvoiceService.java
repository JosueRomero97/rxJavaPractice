package com.mitocode.springreactore.service;

import com.mitocode.springreactore.model.Dish;
import com.mitocode.springreactore.model.Invoice;
import reactor.core.publisher.Mono;


public interface IInvoiceService extends IGenericService<Invoice,String> {

    Mono<byte[]> generateReport(String idInvoice);

}
