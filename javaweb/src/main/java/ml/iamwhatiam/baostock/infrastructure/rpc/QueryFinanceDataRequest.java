package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.Year;

@ToString
public class QueryFinanceDataRequest extends BaoStockRequest {

    /**
     * 股票代码，sh或sz.+6位数字代码，或者指数代码
     */
    @Getter
    private final String code;

    /**
     * 统计年份
     */
    @Getter
    private final Year year;

    /**
     * 统计季度
     */
    @Getter
    private final int quarter;

    private final FinanceType financeType;

    public QueryFinanceDataRequest(String userId, String code, FinanceType financeType) {
        this(userId, code, Year.now(), financeType);
    }

    public QueryFinanceDataRequest(String userId, String code, LocalDate date, FinanceType financeType) {
        this(userId, code, Year.of(date.getYear()), (date.getMonthValue() - 1) / 3 + 1, financeType);
    }

    public QueryFinanceDataRequest(String userId, String code, Year year, FinanceType financeType) {
        this(userId, code, year, (LocalDate.now().getMonthValue() - 1) / 3 + 1, financeType);
    }

    public QueryFinanceDataRequest(String userId, String code, int quarter, FinanceType financeType) {
        this(userId, code, Year.now(), quarter, financeType);
    }

    public QueryFinanceDataRequest(String userId, String code, Year year, int quarter, FinanceType financeType) {
        super(userId);
        this.code = code;
        assert year.isAfter(Year.of(2006));
        this.year = year;
        assert quarter >= 1 && quarter <= 4;
        this.quarter = quarter;
        assert financeType != null;
        this.financeType = financeType;
    }

    @Override
    String getRequestCode() {
        return financeType.getMsgType();
    }

    @Override
    String encode() {
        return financeType.getMethod() + Constants.MESSAGE_SPLIT + userId + Constants.MESSAGE_SPLIT + getCurPageNum()
                + Constants.MESSAGE_SPLIT + getPerPageCount()
                + Constants.MESSAGE_SPLIT + code + Constants.MESSAGE_SPLIT + year.getValue()
                + Constants.MESSAGE_SPLIT + quarter;
    }

    @Getter
    public enum FinanceType {

        PROFIT("query_profit_data", Constants.MESSAGE_TYPE_PROFITDATA_REQUEST),
        OPERATION("query_operation_data", Constants.MESSAGE_TYPE_OPERATIONDATA_REQUEST),
        GROWTH("query_growth_data", Constants.MESSAGE_TYPE_QUERYGROWTHDATA_REQUEST),
        BALANCE("query_balance_data", Constants.MESSAGE_TYPE_QUERYBALANCEDATA_REQUEST),
        CASH_FLOW("query_cash_flow_data", Constants.MESSAGE_TYPE_QUERYCASHFLOWDATA_REQUEST),
        DUPONT("query_dupont_data", Constants.MESSAGE_TYPE_QUERYDUPONTDATA_REQUEST),
        ;

        private final String method;

        private final String msgType;

        FinanceType(String method, String msgType) {
            this.method = method;
            this.msgType = msgType;
        }
    }
}
