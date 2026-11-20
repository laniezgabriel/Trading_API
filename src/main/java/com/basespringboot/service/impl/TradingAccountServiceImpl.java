package com.basespringboot.service.impl;

import com.basespringboot.dto.AccountDto;
import com.basespringboot.entity.TradingAccount;
import com.basespringboot.exception.ResourceNotFoundException;
import com.basespringboot.repository.TradeRepository;
import com.basespringboot.repository.TradingAccountRepository;
import com.basespringboot.service.TradingAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class TradingAccountServiceImpl implements TradingAccountService {

    private final TradingAccountRepository accountRepository;
    private final TradeRepository tradeRepository;

    @Override
    public AccountDto create(AccountDto dto) {
        TradingAccount account = TradingAccount.builder()
                .name(dto.getName())
                .initialCapital(dto.getInitialCapital())
                .build();
        return toDto(accountRepository.save(account));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getById(Long id) {
        return toDto(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAll() {
        return accountRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public AccountDto update(Long id, AccountDto dto) {
        TradingAccount account = findOrThrow(id);
        account.setName(dto.getName());
        account.setInitialCapital(dto.getInitialCapital());
        return toDto(accountRepository.save(account));
    }

    @Override
    public void delete(Long id) {
        accountRepository.delete(findOrThrow(id));
    }

    private TradingAccount findOrThrow(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account " + id + " not found"));
    }

    private AccountDto toDto(TradingAccount account) {
        BigDecimal totalProfitLoss = Objects.requireNonNullElse(
                tradeRepository.sumProfitLossByAccount(account.getId()), BigDecimal.ZERO);
        return AccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .initialCapital(account.getInitialCapital())
                .balance(account.getInitialCapital().add(totalProfitLoss))
                .build();
    }
}
