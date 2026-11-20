package com.basespringboot.service;

import com.basespringboot.dto.TradeDto;

import java.util.List;

public interface TradeService {

    TradeDto create(TradeDto dto);

    TradeDto getById(Long id);

    List<TradeDto> getAll();

    List<TradeDto> getByAccountId(Long accountId);

    TradeDto update(Long id, TradeDto dto);

    void delete(Long id);
}
