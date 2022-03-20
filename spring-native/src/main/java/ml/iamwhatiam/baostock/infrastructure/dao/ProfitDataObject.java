package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString(callSuper = true)
public class ProfitDataObject extends FinanceDataObject {

    /**
     * 净资产收益率(平均):归属母公司股东净利润/[(期初归属母公司股东的权益+期末归属母公司股东的权益)/2]*100%
     */
    private BigDecimal roeAvg;

    /**
     * 销售净利率(%):净利润/营业收入*100%
     */
    private BigDecimal netProfitMargin;

    /**
     * 销售毛利率(%):毛利/营业收入*100%=(营业收入-营业成本)/营业收入*100%
     */
    private BigDecimal grossProfitMargin;

    /**
     * 净利润(元)
     */
    private BigDecimal netProfit;

    /**
     * 每股收益
     */
    private BigDecimal earningsPerShareTimeToMarket;

    /**
     * 主营营业收入(元)
     */
    private BigDecimal primaryBusinessRevenue;

    /**
     * 总股本
     */
    private BigDecimal totalShare;

    /**
     * 流通股本
     */
    private BigDecimal tradableShare;
}
