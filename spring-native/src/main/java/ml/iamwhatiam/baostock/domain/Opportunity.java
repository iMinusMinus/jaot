package ml.iamwhatiam.baostock.domain;

import java.math.BigDecimal;

public class Opportunity extends AbstractValueObject {

    /**
     * 市场增长率
     */
    private BigDecimal grow;

    /**
     * 成本下降率
     */
    private BigDecimal cost;
}
