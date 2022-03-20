package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString(callSuper = true)
public class CashFlowDataObject extends FinanceDataObject {

    /**
     * 流动资产除以总资产
     */
    private BigDecimal currentAssetToAsset;

    /**
     * 非流动资产除以总资产
     */
    private BigDecimal nonCurrentAssetToAsset;

    /**
     * 有形资产除以总资产
     */
    private BigDecimal tangibleAssetToAsset;

    /**
     * 已获利息倍数:息税前利润/利息费用
     */
    private BigDecimal earningsBeforeInterestAndTaxToInterest;

    /**
     * 经营活动产生的现金流量净额除以营业收入
     */
    private BigDecimal operationalCashFlowToOperationalRevenue;

    /**
     * 经营性现金净流量除以净利润
     */
    private BigDecimal operationalCashFlowToNetProfit;

    /**
     * 经营性现金净流量除以营业总收入
     */
    private BigDecimal operationalCashFlowToGrossRevenue;
}
