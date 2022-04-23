package ml.iamwhatiam.baostock.domain;

import lombok.Getter;
import ml.iamwhatiam.baostock.infrastructure.rpc.Constants;

import java.time.LocalDate;

@Getter
public enum StockIndexType {

    SZ50(1, "query_sz50_stocks", Constants.MESSAGE_TYPE_QUERYSZ50STOCKS_REQUEST, LocalDate.of(2004, 1, 2), 6), // 上证50，半年调整一次
    HS300(2, "query_hs300_stocks", Constants.MESSAGE_TYPE_QUERYHS300STOCKS_REQUEST, LocalDate.of(2005, 4, 8), 6), // 沪深300，半年调整一次
    ZZ500(4, "query_zz500_stocks", Constants.MESSAGE_TYPE_QUERYZZ500STOCKS_REQUEST, LocalDate.of(2004, 12, 31), 6), // 中证500，半年调整一次
    ;

    private final int value;

    private final String method;

    private final String msgType;

    private final LocalDate pubDate;

    private final int frequency;

    StockIndexType(int value, String method, String msgType, LocalDate pubDate, int frequency) {
        this.value = value;
        this.method = method;
        this.msgType = msgType;
        this.pubDate = pubDate;
        this.frequency = frequency;
    }

    public static StockIndexType getInstance(int value) {
        for(StockIndexType instance : StockIndexType.values()) {
            if(instance.value == value) {
                return instance;
            }
        }
        return null;
    }
}
