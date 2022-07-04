package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.time.Year;

@Getter
@ToString
public class QueryDividendDataRequest extends BaoStockRequest {

    /**
     * 股票代码，sh或sz.+6位数字代码，或者指数代码
     */
    private final String code;

    /**
     * 年份，默认当前年份
     */
    private final int year;

    /**
     * 年份类别，默认为"report":预案公告年份，可选项"operate":除权除息年份
     */
    private final String yearType;

    public QueryDividendDataRequest(String userId, String code) {
        this(userId, code, Year.now());
    }

    public QueryDividendDataRequest(String userId, String code, Year year) {
        this(userId, code, year, YearType.REPORT);
    }

    public QueryDividendDataRequest(String userId, String code, YearType yearType) {
        this(userId, code, Year.now(), yearType);
    }

    public QueryDividendDataRequest(String userId, String code, Year year, YearType yearType) {
        super(userId);
        assert code != null;
        assert year != null;
        assert yearType != null;
        this.code = code;
        this.year = year.getValue();
        this.yearType = yearType.name().toLowerCase();
    }

    @Override
    String getRequestCode() {
        return Constants.MESSAGE_TYPE_QUERYDIVIDENDDATA_REQUEST;
    }

    @Override
    String encode() {
        return "query_dividend_data" + Constants.MESSAGE_SPLIT + userId + Constants.MESSAGE_SPLIT + getCurPageNum()
                + Constants.MESSAGE_SPLIT + getPerPageCount()
                + Constants.MESSAGE_SPLIT + code + Constants.MESSAGE_SPLIT + year
                + Constants.MESSAGE_SPLIT + yearType;
    }

    public enum YearType {
        REPORT, // 预案公告年份
        OPERATE; // 除权除息年份
    }
}
