package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class DividendDataObject extends AbstractDataObject {

    /**
     * 证券代码
     */
    private String code;

    /**
     * 预批露公告日
     */
    private LocalDate preNoticeDate;

    /**
     * 股东大会公告日期
     */
    private LocalDate agmAnnouncementDate;

    /**
     * 预案公告日
     */
    private LocalDate planAnnounceDate;

    /**
     * 分红实施公告日
     */
    private LocalDate planDate;

    /**
     * 股权登记告日
     */
    private LocalDate registerDate;

    /**
     * 除权除息日期
     */
    private LocalDate operateDate;

    /**
     * 派息日
     */
    private LocalDate payDate;

    /**
     * 红股上市交易日
     */
    private LocalDate marketDate;

    /**
     * 每股股利税前
     */
    private BigDecimal cashPerShareBeforeTax;

    /**
     * 每股股利税后
     */
    private String cashPerShareAfterTax;

    /**
     * 每股红股
     */
    private BigDecimal stockPerShare;

    /**
     * 分红送转
     */
    private String cashStock;

    /**
     * 每股转增资本
     */
    private BigDecimal reserveToStockPerShare;
}
