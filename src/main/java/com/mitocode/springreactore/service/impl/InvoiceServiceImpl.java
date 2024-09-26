package com.mitocode.springreactore.service.impl;

import com.mitocode.springreactore.model.Client;
import com.mitocode.springreactore.model.Invoice;
import com.mitocode.springreactore.model.InvoiceDetail;
import com.mitocode.springreactore.repo.IClientRepo;
import com.mitocode.springreactore.repo.IDishRepo;
import com.mitocode.springreactore.repo.IGenericRepo;
import com.mitocode.springreactore.repo.IInvoiceRepo;
import com.mitocode.springreactore.service.IClientService;
import com.mitocode.springreactore.service.IInvoiceService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl extends CRUDImpl<Invoice,String> implements IInvoiceService {

    private final IInvoiceRepo invoiceRepo;
    private final IClientRepo clientRepo;
    private final IDishRepo iDishRepo;
    @Override
    protected IGenericRepo<Invoice, String> getRepo() {
        return invoiceRepo;
    }

    @Override
    public Mono<byte[]> generateReport(String idInvoice) {
        return invoiceRepo.findById(idInvoice)
                .flatMap(this::populateClient)
                .flatMap(this::populateItems)
                .map(this::generarPDF)
                .onErrorResume(e-> Mono.empty());
    }

    private Mono<Invoice> populateClient(Invoice invoice){
        return clientRepo.findById(invoice.getClient().getId())
                .map(client->{
                    invoice.setClient(client);
                    return invoice;
                });
    }

    private Mono<Invoice> populateItems(Invoice invoice){
        List<Mono<InvoiceDetail>> list = invoice.getItems().stream()
                .map(item-> iDishRepo.findById(item.getDish().getId())
                        .map(dish -> {
                            item.setDish(dish);
                            return item;
                        }))
                .toList();

        return Mono.when(list).then(Mono.just(invoice));
    }

    private byte[] generarPDF(Invoice inv){
        try {
            Map<String,Object> params = new HashMap<>();
            params.put("txt_client", inv.getClient().getFirstName() + " " + inv.getClient().getLastName());

            InputStream jrxml = getClass().getResourceAsStream("/InvoiceReport.jrxml");
            JasperReport jasper = JasperCompileManager.compileReport(jrxml);

            JasperPrint print =  JasperFillManager.fillReport(jasper, params, new JRBeanCollectionDataSource(inv.getItems()));
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException e) {
            System.out.println("existe error");
            System.out.println(e.getMessage().toString() );
            //throw new RuntimeException(e);
            return new byte[0];
        }
    }


   /* @Override
    public Mono<byte[]> generateReport(String idInvoice) {
        return  invoiceRepo.findById(idInvoice)
                .flatMap(inv->
                            Mono.just(inv)
                            .zipWith(clientRepo.findById(inv.getClient().getId()), (in,cl)->{
                                in.setClient(cl);
                                return in;
                            })
                    )
                .flatMap(invoice -> {
                    return Flux.fromIterable(invoice.getItems())
                            .flatMap(item->{
                                return iDishRepo.findById(item.getDish().getId())
                                        .map(d->{
                                            item.setDish(d);
                                            return item;
                                        });
                            }).collectList()
                            .flatMap(list->{
                                invoice.setItems(list);
                                return Mono.just(invoice);
                            });
                })
                .map(inv->{
                        try {
                            Map<String,Object> params = new HashMap<>();
                            params.put("txt_client", inv.getClient().getFirstName() + " " + inv.getClient().getLastName());

                            InputStream jrxml = getClass().getResourceAsStream("/InvoiceReport.jrxml");
                            JasperReport jasper = JasperCompileManager.compileReport(jrxml);

                            JasperPrint print =  JasperFillManager.fillReport(jasper, params, new JRBeanCollectionDataSource(inv.getItems()));
                            return JasperExportManager.exportReportToPdf(print);
                        } catch (JRException e) {
                            System.out.println("existe error");
                            System.out.println(e.getMessage().toString() );
                            //throw new RuntimeException(e);
                            return new byte[0];
                        }
                    });
    }*/
}
