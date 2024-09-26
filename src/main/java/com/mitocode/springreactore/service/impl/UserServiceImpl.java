package com.mitocode.springreactore.service.impl;

import com.mitocode.springreactore.model.User;
import com.mitocode.springreactore.repo.IGenericRepo;
import com.mitocode.springreactore.repo.IUserRepo;
import com.mitocode.springreactore.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends CRUDImpl<User,String> implements IUserService {

    private final IUserRepo repo;
    @Override
    protected IGenericRepo<User, String> getRepo() {
        return repo;
    }
}
