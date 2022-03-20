package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString(callSuper = true)
public class GrowthDataObject extends FinanceDataObject {

    /**
     * 净资产同比增长率:(本期净资产-上年同期净资产)/上年同期净资产的绝对值*100%
     */
    private BigDecimal yearOverYearEquity;

    /**
     * 总资产同比增长率:(本期总资产-上年同期总资产)/上年同期总资产的绝对值*100%
     */
    private BigDecimal yearOverYearAsset;

    /**
     * 净利润同比增长率:(本期净利润-上年同期净利润)/上年同期净利润的绝对值*100%
     */
    private BigDecimal yearOverYearNetProfit;

    /**
     * 基本每股收益同比增长率:(本期基本每股收益-上年同期基本每股收益)/上年同期基本每股收益的绝对值*100%
     */
    private BigDecimal yearOverYearEarningsPerShareBasic;

    /**
     * 归属母公司股东净利润同比增长率:(本期归属母公司股东净利润-上年同期归属母公司股东净利润)/上年同期归属母公司股东净利润的绝对值*100%
     */
    private BigDecimal yearOverYearNetProfitAttributableToShareHoldersOfTheParentCompany;
}
