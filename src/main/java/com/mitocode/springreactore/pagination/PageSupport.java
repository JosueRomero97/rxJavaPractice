package com.mitocode.springreactore.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageSupport<T> {
    public static final String FIRST_PAGE_NUM = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";

    private List<T> content;
    private int pageNumber;//pagina q me encuentro
    private int pageSize;
    private long totalElements;

    @JsonProperty //un atributo más en la salida del json
    public long totalPage(){
        return pageSize>0?(totalElements-1)/ pageSize + 1 : 0;
    }

    @JsonProperty
    public boolean first(){
        return pageNumber == Integer.parseInt(FIRST_PAGE_NUM);
    }

    @JsonProperty
    public boolean last(){
         return (pageNumber+1)*pageSize >= totalElements;
    }





}
