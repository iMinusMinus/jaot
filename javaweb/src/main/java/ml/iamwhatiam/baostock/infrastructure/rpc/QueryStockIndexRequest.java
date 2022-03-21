package ml.iamwhatiam.baostock.infrastructure.rpc;

import ml.iamwhatiam.baostock.domain.StockIndexType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class QueryStockIndexRequest extends BaoStockRequest {

    /**
     * 查询日期，格式XXXX-XX-XX
     */
    private final LocalDate date;

    private final StockIndexType indexType;

    public QueryStockIndexRequest(String userId, StockIndexType indexType) {
        this(userId, indexType, null);
    }

    public QueryStockIndexRequest(String userId, StockIndexType indexType, LocalDate date) {
        super(userId);
        this.indexType = indexType;
        this.date = date;
    }

    @Override
    String getRequestCode() {
        return indexType.getMsgType();
    }

    @Override
    String encode() {
        return indexType.getMethod() + Constants.MESSAGE_SPLIT + userId + Constants.MESSAGE_SPLIT + getCurPageNum()
                + Constants.MESSAGE_SPLIT + getPerPageCount() + Constants.MESSAGE_SPLIT
                + (date == null ? EMPTY : date.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
