package com.basespringboot.service.impl;

import com.basespringboot.dto.TradeDto;
import com.basespringboot.entity.Trade;
import com.basespringboot.entity.TradingAccount;
import com.basespringboot.exception.BusinessRuleException;
import com.basespringboot.exception.ResourceNotFoundException;
import com.basespringboot.repository.TradeRepository;
import com.basespringboot.repository.TradingAccountRepository;
import com.basespringboot.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeServiceImpl implements TradeService {

    // Challenge rules (fraction of the initial capital).
    private static final BigDecimal DAILY_LOSS_LIMIT = new BigDecimal("0.04");
    private static final BigDecimal MAX_LOSS_LIMIT = new BigDecimal("0.12");

    private final TradeRepository tradeRepository;
    private final TradingAccountRepository accountRepository;

    @Override
    public TradeDto create(TradeDto dto) {
        TradingAccount account = findAccountOrThrow(dto.getAccountId());
        LocalDateTime openedAt = dto.getOpenedAt() != null ? dto.getOpenedAt() : LocalDateTime.now();

        checkRiskLimits(account, openedAt);

        Trade trade = Trade.builder()
                .instrument(dto.getInstrument())
                .direction(dto.getDirection())
                .profitLoss(dto.getProfitLoss())
                .openedAt(openedAt)
                .account(account)
                .build();
        return toDto(tradeRepository.save(trade));
    }

    @Override
    @Transactional(readOnly = true)
    public TradeDto getById(Long id) {
        return toDto(findTradeOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TradeDto> getAll() {
        return tradeRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TradeDto> getByAccountId(Long accountId) {
        findAccountOrThrow(accountId);
        return tradeRepository.findByAccountId(accountId).stream().map(this::toDto).toList();
    }

    @Override
    public TradeDto update(Long id, TradeDto dto) {
        Trade trade = findTradeOrThrow(id);
        trade.setInstrument(dto.getInstrument());
        trade.setDirection(dto.getDirection());
        trade.setProfitLoss(dto.getProfitLoss());
        if (dto.getOpenedAt() != null) {
            trade.setOpenedAt(dto.getOpenedAt());
        }
        trade.setAccount(findAccountOrThrow(dto.getAccountId()));
        return toDto(tradeRepository.save(trade));
    }

    @Override
    public void delete(Long id) {
        tradeRepository.delete(findTradeOrThrow(id));
    }

    /**
     * Business rule: once the daily loss (4%) or the total loss (12%) of the
     * initial capital is reached, no new trade can be recorded.
     */
    private void checkRiskLimits(TradingAccount account, LocalDateTime openedAt) {
        LocalDateTime dayStart = openedAt.toLocalDate().atStartOfDay();
        BigDecimal dayNet = Objects.requireNonNullElse(
                tradeRepository.sumProfitLossByAccountBetween(account.getId(), dayStart, dayStart.plusDays(1)),
                BigDecimal.ZERO);
        BigDecimal totalNet = Objects.requireNonNullElse(
                tradeRepository.sumProfitLossByAccount(account.getId()), BigDecimal.ZERO);

        BigDecimal dailyLimit = account.getInitialCapital().multiply(DAILY_LOSS_LIMIT).negate();
        BigDecimal maxLimit = account.getInitialCapital().multiply(MAX_LOSS_LIMIT).negate();

        if (totalNet.compareTo(maxLimit) <= 0) {
            throw new BusinessRuleException("Maximum loss limit (12%) reached: account is closed");
        }
        if (dayNet.compareTo(dailyLimit) <= 0) {
            throw new BusinessRuleException("Daily loss limit (4%) reached: no more trades today");
        }
    }

    private TradingAccount findAccountOrThrow(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account " + id + " not found"));
    }

    private Trade findTradeOrThrow(Long id) {
        return tradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trade " + id + " not found"));
    }

    private TradeDto toDto(Trade trade) {
        return TradeDto.builder()
                .id(trade.getId())
                .instrument(trade.getInstrument())
                .direction(trade.getDirection())
                .profitLoss(trade.getProfitLoss())
                .openedAt(trade.getOpenedAt())
                .accountId(trade.getAccount().getId())
                .build();
    }
}
