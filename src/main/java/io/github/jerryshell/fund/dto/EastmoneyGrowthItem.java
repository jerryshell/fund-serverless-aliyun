package io.github.jerryshell.fund.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EastmoneyGrowthItem {
    private LocalDateTime x;
    private BigDecimal equityReturn;

    public EastmoneyGrowthItem(LocalDateTime x, BigDecimal equityReturn) {
        this.x = x;
        this.equityReturn = equityReturn;
    }

    public LocalDateTime getX() {
        return x;
    }

    public void setX(LocalDateTime x) {
        this.x = x;
    }

    public BigDecimal getEquityReturn() {
        return equityReturn;
    }

    public void setEquityReturn(BigDecimal equityReturn) {
        this.equityReturn = equityReturn;
    }
}
