package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class QueryHistoryKDataPlusRequest extends BaoStockRequest {

    /**
     * 股票代码，sh或sz.+6位数字代码，或者指数代码，如：sh.601398。sh：上海；sz：深圳
     */
    @Getter
    private final String code;

    /**
     * 指示简称，支持多指标输入，以半角逗号分隔，填写内容作为返回类型的列。详细指标列表见历史行情指标参数章节，日线与分钟线参数不同
     */
    @Getter
    private final String fields;

    /**
     * 开始日期（包含），格式“YYYY-MM-DD”，为空时取2015-01-01
     */
    @Getter
    private final LocalDate startDate;

    /**
     * 结束日期（包含），格式“YYYY-MM-DD”，为空时取最近一个交易日
     */
    @Getter
    @Setter
    private LocalDate endDate;

    /**
     * 数据类型，默认为d，日k线；d=日k线、w=周、m=月、5=5分钟、15=15分钟、30=30分钟、60=60分钟k线数据，不区分大小写；指数没有分钟线数据；周线每周最后一个交易日才可以获取，月线每月最后一个交易日才可以获取
     */
    @Getter
    private final QueryHistoryKDataPlusResponse.Frequency frequency;

    /**
     * 复权类型，默认不复权：3；1：后复权；2：前复权。已支持分钟线、日线、周线、月线前后复权
     * refer: http://baostock.com/baostock/images/2/20/BaoStock%E5%A4%8D%E6%9D%83%E5%9B%A0%E5%AD%90%E7%AE%80%E4%BB%8B.pdf
     */
    @Getter
    private final int adjustFlag;

    public QueryHistoryKDataPlusRequest(String userId, String code) {
        this(userId, code, QueryHistoryKDataPlusResponse.Frequency.DAY, 3);
    }

    public QueryHistoryKDataPlusRequest(String userId, String code, QueryHistoryKDataPlusResponse.Frequency frequency, int adjustFlag) {
        this(userId, code, LocalDate.parse(Constants.DEFAULT_START_DATE, DateTimeFormatter.ISO_LOCAL_DATE), LocalDate.now(), frequency, adjustFlag);
    }

    public QueryHistoryKDataPlusRequest(String userId, String code, LocalDate startDate, LocalDate endDate, QueryHistoryKDataPlusResponse.Frequency frequency, int adjustFlag) {
        super(userId);
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        if(QueryHistoryKDataPlusResponse.Frequency.DAY == frequency) {
            this.fields = "date,code,open,high,low,close,preclose,volume,amount,adjustflag,turn,tradestatus,pctChg,peTTM,psTTM,pcfNcfTTM,pbMRQ,isST";
        } else if(frequency == QueryHistoryKDataPlusResponse.Frequency.WEEK || frequency == QueryHistoryKDataPlusResponse.Frequency.MONTH) {
            this.fields = "date,code,open,high,low,close,volume,amount,adjustflag,turn,pctChg";
        } else {
            this.fields = "date,time,code,open,high,low,close,volume,amount,adjustflag";
        }
        this.adjustFlag = adjustFlag;
    }

    @Override
    String getRequestCode() {
        return Constants.MESSAGE_TYPE_GETKDATAPLUS_REQUEST;
    }

    @Override
    String encode() {
        return "query_history_k_data" + Constants.MESSAGE_SPLIT + userId + Constants.MESSAGE_SPLIT + getCurPageNum()
                + Constants.MESSAGE_SPLIT + getPerPageCount()
                + Constants.MESSAGE_SPLIT + code + Constants.MESSAGE_SPLIT + fields
                + Constants.MESSAGE_SPLIT + DateTimeFormatter.ISO_LOCAL_DATE.format(startDate)
                + Constants.MESSAGE_SPLIT + (endDate == null ? EMPTY : DateTimeFormatter.ISO_LOCAL_DATE.format(endDate))
                + Constants.MESSAGE_SPLIT + frequency.getFrequency() + Constants.MESSAGE_SPLIT + adjustFlag;
    }
}
