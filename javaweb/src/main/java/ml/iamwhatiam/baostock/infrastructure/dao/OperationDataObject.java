package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString(callSuper = true)
public class OperationDataObject extends FinanceDataObject {

    /**
     * 应收账款周转率(次):营业收入/[(期初应收票据及应收账款净额+期末应收票据及应收账款净额)/2]
     */
    private BigDecimal noteReceiveTurnRatio;

    /**
     * 应收账款周转天数(天):季报天数/应收账款周转率(一季报：90天，中报：180天，三季报：270天，年报：360天)
     */
    private BigDecimal noteReceiveTurnDays;

    /**
     * 存货周转率(次):营业成本/[(期初存货净额+期末存货净额)/2]
     */
    private BigDecimal inventoryTurnRatio;

    /**
     * 存货周转天数(天):季报天数/存货周转率(一季报：90天，中报：180天，三季报：270天，年报：360天)
     */
    private BigDecimal inventoryTurnDays;

    /**
     * 流动资产周转率(次):营业总收入/[(期初流动资产+期末流动资产)/2]
     */
    private BigDecimal currentAssetTurnRatio;

    /**
     * 总资产周转率:营业总收入/[(期初资产总额+期末资产总额)/2]
     */
    private BigDecimal assetTurnRatio;
}
