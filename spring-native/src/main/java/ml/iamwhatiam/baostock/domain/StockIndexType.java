package ml.iamwhatiam.baostock.domain;

import lombok.Getter;
import ml.iamwhatiam.baostock.infrastructure.rpc.Constants;

@Getter
public enum StockIndexType {

    SZ50(1, "query_sz50_stocks", Constants.MESSAGE_TYPE_QUERYSZ50STOCKS_REQUEST), // 上证50
    HS300(2, "query_hs300_stocks", Constants.MESSAGE_TYPE_QUERYHS300STOCKS_REQUEST), // 沪深300
    ZZ500(4, "query_zz500_stocks", Constants.MESSAGE_TYPE_QUERYZZ500STOCKS_REQUEST), // 中证500
    ;

    private final int value;

    private final String method;

    private final String msgType;

    StockIndexType(int value, String method, String msgType) {
        this.value = value;
        this.method = method;
        this.msgType = msgType;
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
