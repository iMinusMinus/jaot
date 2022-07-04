package ml.iamwhatiam.baostock.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 股价上涨机会：
 * <ul>
 *     <li>营收增长</li>
 *     <li>成本下降</li>
 * </ul>
 */
@Getter
@Setter
@ToString
public class Opportunity extends AbstractValueObject {

    /**
     * 营收增长率
     */
    private BigDecimal grow = BigDecimal.ZERO;

    /**
     * 利润增长率
     */
    private BigDecimal netGrow = BigDecimal.ZERO;

    /**
     * 成本下降率
     */
    private BigDecimal cost = BigDecimal.ZERO;
}
