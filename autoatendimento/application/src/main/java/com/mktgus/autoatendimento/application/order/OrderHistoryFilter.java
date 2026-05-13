package com.mktgus.autoatendimento.application.order;

import java.time.LocalDateTime;

public record OrderHistoryFilter(
        Long marketId,
        LocalDateTime from,
        LocalDateTime to,
        Integer limit
) {
}
