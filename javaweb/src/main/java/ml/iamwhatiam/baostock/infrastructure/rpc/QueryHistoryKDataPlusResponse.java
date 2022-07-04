package ml.iamwhatiam.baostock.infrastructure.rpc;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ToString(callSuper = true)
public class QueryHistoryKDataPlusResponse extends BaoStockResponse {

    @Getter
    private int curPageNum;

    @Getter
    private int perPageCount;

    @Getter
    private String code;

    @Getter
    private List<QueryHistoryKDataPlusResponse.Quotation> data;

    private String[] fields;

    @Getter
    private LocalDate startDate;

    @Getter
    private LocalDate endDate;

    @Getter
    private Frequency frequency;

    @Getter
    private int adjustFlag;

    public QueryHistoryKDataPlusResponse(String[] headerArray, String[] bodyArray) {
        super(headerArray, bodyArray);
        if(isSuccess()) {
            curPageNum = Integer.parseInt(bodyArray[4]);
            perPageCount = Integer.parseInt(bodyArray[5]);
            List<List<String>> tmp = JsonParser.parseRecord(bodyArray[6]);
            data = new ArrayList<>(tmp.size());
            code = bodyArray[7];
            fields = bodyArray[8].split(Constants.ATTRIBUTE_SPLIT);
            startDate = LocalDate.parse(bodyArray[9], DateTimeFormatter.ISO_LOCAL_DATE);
            endDate = LocalDate.parse(bodyArray[10], DateTimeFormatter.ISO_LOCAL_DATE);
            frequency = Frequency.getInstance(bodyArray[11].toLowerCase(Locale.ENGLISH));
            adjustFlag = Integer.parseInt(bodyArray[12]);
            for(List<String> quotation : tmp) {
                data.add(new Quotation(quotation.toArray(new String[0]), frequency));
            }
        }
    }

    public enum Frequency {

        TWELFTH("5"),
        QUARTER("15"),
        HALF("30"),
        HOUR("60"),
        DAY("d"),
        WEEK("w"),
        MONTH("m"),
        ;

        @Getter
        @JsonValue
        private final String frequency;

        Frequency(String frequency) {
            this.frequency = frequency;
        }

        public static Frequency getInstance(String frequency) {
            for(Frequency value : Frequency.values()) {
                if(value.frequency.equals(frequency)) {
                    return value;
                }
            }
            return null;
        }
    }

    @Getter
    @ToString
    public static class Quotation {

        /**
         * 交易所行情日期
         */
        private final LocalDate date;

        /**
         * 交易所行情时间:格式：YYYYMMDDHHMMSSsss
         */
        private LocalDateTime time;

        /**
         * 证券代码
         */
        private final String code;

        /**
         * 开盘价
         */
        private final BigDecimal open;

        /**
         * 最高价
         */
        private final BigDecimal high;

        /**
         * 最高价
         */
        private final BigDecimal low;

        /**
         * 收盘价
         */
        private final BigDecimal close;

        /**
         * 前收盘价
         */
        private BigDecimal preClose;

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
        private final int adjustFlag;

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
        private BigDecimal pctChg;

        /**
         * 滚动市盈率:(指定交易日的股票收盘价/指定交易日的每股盈余TTM)=(指定交易日的股票收盘价*截至当日公司总股本)/归属母公司股东净利润TTM
         */
        private BigDecimal peTTM;

        /**
         * 市净率:(指定交易日的股票收盘价/指定交易日的每股净资产)=总市值/(最近披露的归属母公司股东的权益-其他权益工具)
         */
        private BigDecimal pbMRQ;

        /**
         * 滚动市销率:(指定交易日的股票收盘价/指定交易日的每股销售额)=(指定交易日的股票收盘价*截至当日公司总股本)/营业总收入TTM
         */
        private BigDecimal psTTM;

        /**
         * 滚动市现率:(指定交易日的股票收盘价/指定交易日的每股现金流TTM)=(指定交易日的股票收盘价*截至当日公司总股本)/现金以及现金等价物净增加额TTM
         */
        private BigDecimal pcfNcfTTM;

        /**
         * 是否ST股，1是，0否
         */
        private boolean st;

        public Quotation(String[] data, Frequency frequency) {
            date = LocalDate.parse(data[0], DateTimeFormatter.ISO_LOCAL_DATE);
            int index = 1;
            if(frequency == Frequency.TWELFTH || frequency == Frequency.QUARTER || frequency == Frequency.HALF || frequency == Frequency.HOUR) {
                time = LocalDateTime.parse(data[1], DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
                index++;
            }
            code = data[index++];
            open = new BigDecimal(data[index++]);
            high = new BigDecimal(data[index++]);
            low = new BigDecimal(data[index++]);
            close = new BigDecimal(data[index++]);
            if(frequency == Frequency.DAY) {
                preClose = new BigDecimal(data[index++]);
            }
            if(data[index] != null && data[index].length() > 0) {
                volume = Long.parseLong(data[index]);
            }
            index++;
            if(data[index] != null && data[index].length() > 0) {
                amount = new BigDecimal(data[index]);
            }
            index++;
            adjustFlag = Integer.parseInt(data[index++]);
            if(frequency == Frequency.DAY || frequency == Frequency.WEEK || frequency == Frequency.MONTH) {
                if(data[index] != null && data[index].length() > 0) {
                    turn = new BigDecimal(data[index++]);
                } else {
                    turn = null;
                    index++;
                }
                if(frequency == Frequency.DAY) {
                    tradeStatus = Integer.parseInt(data[index++]);
                }
                if(data[index] != null && data[index].length() > 0) {
                    pctChg = new BigDecimal(data[index]);
                }
                index++;
                if(frequency == Frequency.DAY) {
                    peTTM = new BigDecimal(data[index++]);
                    psTTM = new BigDecimal(data[index++]);
                    pcfNcfTTM = new BigDecimal(data[index++]);
                    pbMRQ = new BigDecimal(data[index++]);
                    st = Boolean.parseBoolean(data[index]);
                }
            }
        }

    }
}
