package com.mitocode.springreactore.service.impl;

import com.mitocode.springreactore.model.Client;
import com.mitocode.springreactore.model.Menu;
import com.mitocode.springreactore.repo.IClientRepo;
import com.mitocode.springreactore.repo.IGenericRepo;
import com.mitocode.springreactore.repo.IMenuRepo;
import com.mitocode.springreactore.service.IClientService;
import com.mitocode.springreactore.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends CRUDImpl<Menu,String> implements IMenuService {

    private final IMenuRepo repo;
    @Override
    protected IGenericRepo<Menu, String> getRepo() {
        return repo;
    }
}
