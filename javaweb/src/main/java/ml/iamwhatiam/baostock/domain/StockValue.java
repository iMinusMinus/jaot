package ml.iamwhatiam.baostock.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class StockValue extends AbstractValueObject {

    /**
     * 每股当前利润需要多少年才能达到每股当前价格
     */
    private BigDecimal pe;

    /**
     * 每股净资产率，值越小破产后风险越小
     */
    private BigDecimal pb;

    /**
     * 自定义：每股股息/每股股价
     */
    private BigDecimal pd = BigDecimal.ZERO;

    /**
     * 自定义：每股送转股数
     */
    private BigDecimal pr = BigDecimal.ZERO;
}
