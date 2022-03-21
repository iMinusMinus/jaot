package ml.iamwhatiam.baostock.infrastructure.web;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class StockVO implements Serializable, Comparable<StockVO> {

    @JsonAlias(value = {"top"})
    private Integer position;

    private String code;

    private String codeName;

    private String industry;

    /**
     * price to earnings
     */
    @JsonAlias("priceToEarnings")
    private BigDecimal pe;

    /**
     * price to book
     */
    @JsonAlias("priceToBook")
    private BigDecimal pb;


    /**
     * 每股分红率=预估每股分红/当前每股股价
     */
    private BigDecimal pd;

    /**
     * 预估每股送股/转股率
     */
    private BigDecimal pr;

    @Override
    public int compareTo(StockVO other) {
        // FIXME
        return 0;
    }
}
