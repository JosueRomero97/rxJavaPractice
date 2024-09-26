package com.mitocode.springreactore.config;

import com.mitocode.springreactore.dto.ClientDto;
import com.mitocode.springreactore.dto.InvoiceDto;
import com.mitocode.springreactore.model.Client;
import com.mitocode.springreactore.model.Invoice;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class ConfigMapper {

    @Bean(name = "defaultMapper")
    public ModelMapper defaultMapper(){
        return new ModelMapper();
    }

    @Bean(name = "clientMapper")
    public ModelMapper clientMapper(){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        //ESCRITURA
            mapper.createTypeMap(ClientDto.class,Client.class)
                    .addMapping(ClientDto::getName,(dest,v)-> dest.setFirstName((String) v))
                    .addMapping(ClientDto::getSurname,(dest,v)->dest.setLastName((String) v))
                    .addMapping(ClientDto::getBirthDateClient, (dest,v)-> dest.setBirthDate((LocalDate) v))
                    .addMapping(ClientDto::getPicture, (dest,v)-> dest.setUrlPhoto((String) v));
        //LECTURA
        mapper.createTypeMap(Client.class, ClientDto.class)
                .addMapping(Client::getFirstName, (dest, v)-> dest.setName((String) v))
                .addMapping(Client::getLastName,(dest, v)->dest.setSurname((String) v))
                .addMapping(Client::getBirthDate,(dest,v)-> dest.setBirthDateClient((LocalDate) v))
                .addMapping(Client::getUrlPhoto,(dest,v)-> dest.setPicture((String) v));
        return mapper;
    }

    @Bean(name = "invoiceMapper")
    public ModelMapper invoiceMapper(){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        //ESCRITURA
        mapper.createTypeMap(InvoiceDto.class, Invoice.class)
                .addMapping(e-> e.getClient().getName(),(dest,v)-> dest.getClient().setFirstName((String) v))
                .addMapping(e->e.getClient().getSurname(),(dest,v)->dest.getClient().setLastName( (String) v));

        //LECTURA
        mapper.createTypeMap(Invoice.class, InvoiceDto.class)
                .addMapping(e->e.getClient().getFirstName(), (dest, v)-> dest.getClient().setName((String) v))
                .addMapping(e->e.getClient().getLastName(),(dest, v)->dest.getClient().setSurname((String) v));

        return mapper;

    }

}
