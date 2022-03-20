package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;

public class QueryStockBasicRequest extends BaoStockRequest {

    @Getter
    private final String code;

    @Getter
    private final String codeName;

    public QueryStockBasicRequest(String userId, String code) {
        this(userId, code, EMPTY);
    }

    public QueryStockBasicRequest(String userId, String code, String codeName) {
        super(userId);
        this.code = code;
        this.codeName = codeName;
    }

    @Override
    String getRequestCode() {
        return Constants.MESSAGE_TYPE_QUERYSTOCKBASIC_REQUEST;
    }

    @Override
    String encode() {
        return "query_stock_basic" + Constants.MESSAGE_SPLIT + userId + Constants.MESSAGE_SPLIT + getCurPageNum()
                + Constants.MESSAGE_SPLIT + getPerPageCount() + Constants.MESSAGE_SPLIT + code
                + Constants.MESSAGE_SPLIT + codeName;
    }
}
