package ml.iamwhatiam.baostock.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class StockValue extends AbstractValueObject {

    private BigDecimal pe;

    private BigDecimal pb;

    private BigDecimal pd = BigDecimal.ZERO;

    private BigDecimal pr = BigDecimal.ZERO;
}
