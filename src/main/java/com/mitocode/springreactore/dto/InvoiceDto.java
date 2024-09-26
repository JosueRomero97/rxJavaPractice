package com.mitocode.springreactore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mitocode.springreactore.model.Invoice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceDto {

    private String id;
    private String description;
    private ClientDto client;
    private List<InvoiceDetailDto> items;


}
