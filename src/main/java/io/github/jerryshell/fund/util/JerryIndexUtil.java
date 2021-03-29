package io.github.jerryshell.fund.util;

import io.github.jerryshell.fund.entity.FundGrowth;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class JerryIndexUtil {
    public static BigDecimal calculateByFundGrowthList(List<FundGrowth> fundGrowthList) {

        // dateStr 倒序排序
        fundGrowthList.sort((fundGrowth1, fundGrowth2) -> fundGrowth2.getDateStr().compareTo(fundGrowth1.getDateStr()));

        // fundGrowthList -> fundGrowthValueList
        List<BigDecimal> fundGrowthValueList = fundGrowthList.stream()
                .map(FundGrowth::getGrowth)
                .collect(Collectors.toList());

        return calculateByFundGrowthValueList(fundGrowthValueList);
    }

    // 要求 fundGrowthValueList 的排序顺序为时间倒序
    public static BigDecimal calculateByFundGrowthValueList(List<BigDecimal> fundGrowthValueList) {
        List<BigDecimal> d5 = fundGrowthValueList.stream()
                .limit(5L)
                .collect(Collectors.toList());

        BigDecimal d5sum = d5.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BigDecimal> d123 = fundGrowthValueList.stream()
                .limit(123L)
                .collect(Collectors.toList());

        BigDecimal d123SumD5Avg = d123.parallelStream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(25), RoundingMode.HALF_UP);

        return d5sum.subtract(d123SumD5Avg);
    }
}
