package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ToString
public class QueryStockIndustryRequest extends BaoStockRequest {

    /**
     * A股股票代码，sh或sz.+6位数字代码，或者指数代码，如：sh.601398。sh：上海；sz：深圳。可以为空
     */
    @Getter
    private final String code;

    /**
     * 查询日期，格式XXXX-XX-XX，为空时默认最新日期
     */
    @Getter
    private final LocalDate date;

    public QueryStockIndustryRequest(String userId) {
        this(userId, null, null);
    }

    public QueryStockIndustryRequest(String userId, String code) {
        this(userId, code, null);
    }

    public QueryStockIndustryRequest(String userId, LocalDate date) {
        this(userId, null, null);
    }

    public QueryStockIndustryRequest(String userId, String code, LocalDate date) {
        super(userId);
        this.code = code;
        this.date = date;
    }

    @Override
    String getRequestCode() {
        return Constants.MESSAGE_TYPE_QUERYSTOCKINDUSTRY_REQUEST;
    }

    @Override
    String encode() {
        return "query_stock_industry" + Constants.MESSAGE_SPLIT + userId + Constants.MESSAGE_SPLIT + ONE
                + Constants.MESSAGE_SPLIT + Constants.BAOSTOCK_PER_PAGE_COUNT
                + Constants.MESSAGE_SPLIT + (code == null ? EMPTY : code)
                + Constants.MESSAGE_SPLIT + (date == null ? EMPTY : DateTimeFormatter.ISO_LOCAL_DATE.format(date));
    }
}
