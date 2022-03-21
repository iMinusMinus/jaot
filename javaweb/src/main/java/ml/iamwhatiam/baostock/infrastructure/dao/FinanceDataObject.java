package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public abstract class FinanceDataObject extends AbstractDataObject {

    /**
     * 证券代码
     */
    protected String code;

    /**
     * 公司发布财报的日期
     */
    protected LocalDate pubDate;

    /**
     * 财报统计的季度的最后一天
     */
    protected LocalDate statDate;
}
