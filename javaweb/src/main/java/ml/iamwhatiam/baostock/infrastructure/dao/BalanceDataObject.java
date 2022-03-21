package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString(callSuper = true)
public class BalanceDataObject extends FinanceDataObject {

    /**
     * 流动比率:流动资产/流动负债
     */
    private BigDecimal currentRatio;

    /**
     * 速动比率:(流动资产-存货净额)/流动负债
     */
    private BigDecimal quickRatio;

    /**
     * 现金比率:(货币资金+交易性金融资产)/流动负债
     */
    private BigDecimal cashRatio;

    /**
     * 总负债同比增长率:(本期总负债-上年同期总负债)/上年同期中负债的绝对值*100%
     */
    private BigDecimal yearOverYearLiability;

    /**
     * 资产负债率:负债总额/资产总额
     */
    private BigDecimal liabilityToAsset;

    /**
     * 权益乘数:资产总额/股东权益总额=1/(1-资产负债率)
     */
    private BigDecimal assetToEquity;
}
