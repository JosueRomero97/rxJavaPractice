package com.mitocode.springreactore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class InvoiceDetail {
    private int quality;
    private Dish dish;
}
