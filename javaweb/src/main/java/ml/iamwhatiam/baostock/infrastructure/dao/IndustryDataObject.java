package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class IndustryDataObject extends AbstractDataObject {

    /**
     * 更新日期
     */
    private LocalDate updateDate;

    /**
     * 证券代码
     */
    private String code;

    /**
     * 证券名称
     */
    private String codeName;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 所属行业类别
     */
    private String industryClassification;
}
