package com.mitocode.springreactore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishDto {


    private String id;
    @NotNull
    @Size(min = 2,max = 20)
    private String nameDish;
    @NotNull
    private String priceDish;
    @NotNull
    private Boolean statusDish;

}
