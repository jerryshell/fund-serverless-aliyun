package io.github.jerryshell.fund.entity;

import java.math.BigDecimal;

public class FundGrowth {
    private String code;
    // yyyy-MM-dd
    private String dateStr;
    private BigDecimal growth;

    public FundGrowth(String code, String dateStr, BigDecimal growth) {
        this.code = code;
        this.dateStr = dateStr;
        this.growth = growth;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public BigDecimal getGrowth() {
        return growth;
    }

    public void setGrowth(BigDecimal growth) {
        this.growth = growth;
    }
}
