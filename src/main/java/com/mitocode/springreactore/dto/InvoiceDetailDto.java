package com.mitocode.springreactore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailDto {
    private int quantity;
    private DishDto dish;

}
