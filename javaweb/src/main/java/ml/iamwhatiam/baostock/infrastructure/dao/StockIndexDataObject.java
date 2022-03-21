package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class StockIndexDataObject extends AbstractDataObject {

    /**
     * 证券代码
     */
    private String code;

    /**
     * 证券名称
     */
    private String codeName;

    /**
     * 更新日期
     */
    private LocalDate updateDate;

    /**
     * 纳入该指数日期
     */
    private LocalDate inclusionDate;

    /**
     * 剔除出该指数日期
     */
    private LocalDate exclusionDate;

    /**
     * 指数类型
     * @see ml.iamwhatiam.baostock.domain.StockIndexType
     */
    private int indexType;

}
