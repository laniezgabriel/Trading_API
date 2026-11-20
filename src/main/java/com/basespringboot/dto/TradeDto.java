package com.basespringboot.dto;

import com.basespringboot.entity.TradeDirection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeDto {

    private Long id;

    @NotBlank(message = "instrument is required")
    private String instrument;

    @NotNull(message = "direction is required (BUY or SELL)")
    private TradeDirection direction;

    @NotNull(message = "profitLoss is required")
    private BigDecimal profitLoss;

    // Optional: defaults to now when missing.
    private LocalDateTime openedAt;

    @NotNull(message = "accountId is required")
    private Long accountId;
}
