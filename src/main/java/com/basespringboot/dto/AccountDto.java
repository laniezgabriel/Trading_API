package com.basespringboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Long id;

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "initialCapital is required")
    @Positive(message = "initialCapital must be positive")
    private BigDecimal initialCapital;

    // Computed by the service (initialCapital + sum of trades). Ignored on input.
    private BigDecimal balance;
}
