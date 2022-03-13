package ml.iamwhatiam.baostock.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
public class IndustryEntity extends AbstractEntity {

    private String industry;

    private String industryClassification;

    private BigDecimal avgPe;

    private BigDecimal avgPb;

    private BigDecimal avgPd;

    private BigDecimal avgPr;

    /**
     * 市场规模（元）
     */
    private int market;

    /**
     * 市场利润（元）
     */
    private int profit;

    private List<Opportunity> opportunities;

    private List<Risk> risks;

    private List<StockEntity> stocks;
}
