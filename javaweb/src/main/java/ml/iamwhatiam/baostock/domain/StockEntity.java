package ml.iamwhatiam.baostock.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class StockEntity extends AbstractEntity {

    /**
     * 证券代码
     */
    private String code;

    /**
     * 证券名称
     */
    private String codeName;

    /**
     * 上市日期
     */
    private LocalDate ipoDate;

    /**
     * 退市日期
     */
    private LocalDate outDate;

    /**
     * 证券类型，其中1：股票，2：指数，3：其它，4：可转债，5：ETF
     */
    private Integer type;

    /**
     * 上市状态，其中1：上市，0：退市
     */
    private Integer status;

    /**
     * 营收（元）
     */
    private int revenue;

    /**
     * 市场利润（元）
     */
    private int profit;

    private StockValue value;

    private List<Opportunity> opportunities;

    private List<Risk> risks;
}
