package com.basespringboot.controller;

import com.basespringboot.dto.TradeDto;
import com.basespringboot.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/trades")
    @ResponseStatus(HttpStatus.CREATED)
    public TradeDto create(@Valid @RequestBody TradeDto dto) {
        return tradeService.create(dto);
    }

    @GetMapping("/trades")
    public List<TradeDto> getAll() {
        return tradeService.getAll();
    }

    @GetMapping("/trades/{id}")
    public TradeDto getById(@PathVariable Long id) {
        return tradeService.getById(id);
    }

    @GetMapping("/accounts/{accountId}/trades")
    public List<TradeDto> getByAccountId(@PathVariable Long accountId) {
        return tradeService.getByAccountId(accountId);
    }

    @PutMapping("/trades/{id}")
    public TradeDto update(@PathVariable Long id, @Valid @RequestBody TradeDto dto) {
        return tradeService.update(id, dto);
    }

    @DeleteMapping("/trades/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        tradeService.delete(id);
    }
}
