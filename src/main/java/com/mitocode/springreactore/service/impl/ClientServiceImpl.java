package com.mitocode.springreactore.service.impl;

import com.mitocode.springreactore.model.Client;
import com.mitocode.springreactore.repo.IClientRepo;
import com.mitocode.springreactore.repo.IGenericRepo;
import com.mitocode.springreactore.service.IClientService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl extends CRUDImpl<Client,String> implements IClientService {

    private final IClientRepo repo;
    @Override
    protected IGenericRepo<Client, String> getRepo() {
        return repo;
    }
}
