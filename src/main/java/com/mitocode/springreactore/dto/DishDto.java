package com.mitocode.springreactore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
