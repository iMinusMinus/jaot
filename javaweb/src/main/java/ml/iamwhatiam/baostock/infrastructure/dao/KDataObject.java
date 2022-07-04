package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class KDataObject extends AbstractDataObject {

    /**
     * 交易所行情日期
     */
    private LocalDate date;

    /**
     * 交易所行情时间:格式：YYYYMMDDHHMMSSsss
     */
    private LocalDateTime time;

    /**
     * 证券代码
     */
    private String code;

    /**
     * 开盘价
     */
    private BigDecimal openingPrice;

    /**
     * 最高价
     */
    private BigDecimal highPrice;

    /**
     * 最高价
     */
    private BigDecimal lowPrice;

    /**
     * 收盘价
     */
    private BigDecimal closingPrice;

    /**
     * 前收盘价
     */
    private BigDecimal preClosePrice;

    /**
     * 成交量（累计 单位：股）
     */
    private Long volume;

    /**
     * 成交额（单位：人民币元）
     */
    private BigDecimal amount;

    /**
     * 复权状态(1：后复权， 2：前复权，3：不复权）
     */
    private int adjustFlag;

    /**
     * 换手率: [指定交易日的成交量(股)/指定交易日的股票的流通股总股数(股)]*100%
     */
    private BigDecimal turn;

    /**
     * 交易状态(1：正常交易 0：停牌）
     */
    private int tradeStatus;

    /**
     * 涨跌幅（百分比）:日涨跌幅=[(指定交易日的收盘价-指定交易日前收盘价)/指定交易日前收盘价]*100%
     */
    private BigDecimal changePercent;

    /**
     * 滚动市盈率:(指定交易日的股票收盘价/指定交易日的每股盈余TTM)=(指定交易日的股票收盘价*截至当日公司总股本)/归属母公司股东净利润TTM
     * TTM: trailing twelve months
     * LYR: last year ratio
     */
    private BigDecimal trailingTwelveMonthsPriceToEarningRatio;

    /**
     * 市净率:(指定交易日的股票收盘价/指定交易日的每股净资产)=总市值/(最近披露的归属母公司股东的权益-其他权益工具)
     * MRQ: most recent quarter
     */
    private BigDecimal mostRecentQuarterPriceToBookRatio;

    /**
     * 滚动市销率:(指定交易日的股票收盘价/指定交易日的每股销售额)=(指定交易日的股票收盘价*截至当日公司总股本)/营业总收入TTM
     */
    private BigDecimal trailingTwelveMonthsPriceToSaleRatio;

    /**
     * 滚动市现率:(指定交易日的股票收盘价/指定交易日的每股现金流TTM)=(指定交易日的股票收盘价*截至当日公司总股本)/现金以及现金等价物净增加额TTM
     * PCF: price to cash flow. PCF=股价/每股现金流
     * NCF: net cash flow. 净现金流量=营业收入-付现成本-所得税. 净现金流量=净利润+折旧= (营业收入-相关现金流出-折旧)* (1-税率)+折旧
     */
    private BigDecimal trailingTwelveMonthsPriceToCashFlow; // FIXME

    /**
     * 是否ST股，1是，0否
     */
    private boolean specialTreatment;

    private String frequency;
}
