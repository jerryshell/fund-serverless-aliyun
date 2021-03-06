package io.github.jerryshell.fund.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.github.jerryshell.fund.dto.EastmoneyGrowthItem;
import io.github.jerryshell.fund.entity.FundGrowth;
import io.github.jerryshell.fund.util.JerryIndexUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class FundService {
    public BigDecimal getJerryIndexByFundCode(
            String fundCode
    ) {
        // get data
        List<EastmoneyGrowthItem> eastmoneyGrowthItemList = getEastmoneyGrowthItemList(fundCode);

        // get expect growth
        BigDecimal expectGrowth = getExpectGrowthFromEastmoney(fundCode);

        // build fundGrowthList
        List<FundGrowth> fundGrowthList = buildFundGrowthListByEastmoneyItemList(fundCode, eastmoneyGrowthItemList);

        // handle expect growth
        handleExpectGrowth(fundCode, fundGrowthList, expectGrowth);

        // result
        return JerryIndexUtil.calculateByFundGrowthList(fundGrowthList);
    }

    // eastmoneyGrowthItemList -> fundGrowthList
    private List<FundGrowth> buildFundGrowthListByEastmoneyItemList(String fundCode, List<EastmoneyGrowthItem> eastmoneyGrowthItemList) {
        return eastmoneyGrowthItemList.parallelStream()
                .map(item -> new FundGrowth(
                        fundCode,
                        item.getX().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        item.getEquityReturn()
                ))
                .collect(Collectors.toList());
    }

    // 如果 fundGrowthList 没有今日数据，则将 expectGrowth 作为今日数据加入到 fundGrowthList 中
    private void handleExpectGrowth(String fundCode, List<FundGrowth> fundGrowthList, BigDecimal expectGrowth) {
        String todayDateStr = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        boolean match = fundGrowthList.parallelStream()
                .anyMatch(fundGrowth -> todayDateStr.equals(fundGrowth.getDateStr()));

        if (!match) {
            fundGrowthList.add(new FundGrowth(
                    fundCode,
                    todayDateStr,
                    expectGrowth
            ));
        }
    }

    // 从 fund.eastmoney.com/pingzhongdata 中获取增长率数据
    private List<EastmoneyGrowthItem> getEastmoneyGrowthItemList(String fundCode) {
        String url = StrUtil.format(
                "https://fund.eastmoney.com/pingzhongdata/{}.js",
                fundCode
        );
        String responseStr = HttpUtil.get(url);

        int jsonStrBeginIndex = responseStr.indexOf("Data_netWorthTrend = ") + "Data_netWorthTrend = ".length();

        int jsonStrEndIndex = responseStr.indexOf(";/*累计净值走势*/var Data_ACWorthTrend");

        String jsonStr = responseStr.substring(jsonStrBeginIndex, jsonStrEndIndex);

        return JSONUtil.toList(jsonStr, EastmoneyGrowthItem.class);
    }

    // 从 fundmobapi.eastmoney.com 中获取估算增长率
    private BigDecimal getExpectGrowthFromEastmoney(String fundCode) {
        String url = StrUtil.format(
                "https://fundmobapi.eastmoney.com/FundMNewApi/FundMNFInfo?plat=Android&appType=ttjj&product=EFund&Version=1&deviceid=ssdfsdfsdf&Fcodes={}",
                fundCode
        );

        String responseStr = HttpUtil.get(url);

        JSONObject responseJson = JSONUtil.parseObj(responseStr);

        JSONArray datas = responseJson.getJSONArray("Datas");

        Object expectGrowthObj = datas.getJSONObject(0).get("GSZZL");

        return new BigDecimal((String) expectGrowthObj);
    }
}
