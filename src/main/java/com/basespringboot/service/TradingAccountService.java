package com.basespringboot.service;

import com.basespringboot.dto.AccountDto;

import java.util.List;

public interface TradingAccountService {

    AccountDto create(AccountDto dto);

    AccountDto getById(Long id);

    List<AccountDto> getAll();

    AccountDto update(Long id, AccountDto dto);

    void delete(Long id);
}
