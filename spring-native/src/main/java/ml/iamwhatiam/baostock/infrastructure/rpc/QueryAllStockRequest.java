package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ToString
public class QueryAllStockRequest extends BaoStockRequest{

    /**
     * 需要查询的交易日期
     */
    @Getter
    private final LocalDate day;

    public QueryAllStockRequest(String userId) {
        this(userId, LocalDate.now());
    }

    public QueryAllStockRequest(String userId, LocalDate day) {
        super(userId);
        this.day = day;
    }

    @Override
    String getRequestCode() {
        return Constants.MESSAGE_TYPE_QUERYALLSTOCK_REQUEST;
    }

    @Override
    String encode() {
        return "query_all_stock" + Constants.MESSAGE_SPLIT + userId + Constants.MESSAGE_SPLIT + getCurPageNum()
                + Constants.MESSAGE_SPLIT + getPerPageCount()
                + Constants.MESSAGE_SPLIT + DateTimeFormatter.ISO_LOCAL_DATE.format(day);
    }
}
