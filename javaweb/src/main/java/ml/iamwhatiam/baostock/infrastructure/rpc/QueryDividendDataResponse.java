package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true)
public class QueryDividendDataResponse extends BaoStockResponse {

    @Getter
    private int curPageNum;

    @Getter
    private int perPageCount;

    @Getter
    private String code;

    private int year;

    private String yearType;

    @Getter
    private List<QueryDividendDataResponse.Dividend> data;

    private String[] fields;


    public QueryDividendDataResponse(String[] headerArray, String[] bodyArray) {
        super(headerArray, bodyArray);
        if(isSuccess()) {
            curPageNum = Integer.parseInt(bodyArray[4]);
            perPageCount = Integer.parseInt(bodyArray[5]);
            List<List<String>> tmp = JsonParser.parseRecord(bodyArray[6]);
            code = bodyArray[7];
            year = Integer.parseInt(bodyArray[8]);
            yearType = bodyArray[9];
            fields = bodyArray[10].split(Constants.ATTRIBUTE_SPLIT);
            data = new ArrayList<>(tmp.size());
            for(List<String> list : tmp) {
                data.add(new QueryDividendDataResponse.Dividend(list.toArray(new String[0])));
            }
        }
    }

    @Getter
    @ToString
    public static class Dividend implements Serializable {

        /**
         * 证券代码
         */
        private final String code;

        /**
         * 预批露公告日
         */
        private LocalDate dividPreNoticeDate;

        /**
         * 股东大会公告日期
         */
        private LocalDate dividAgmPumDate;

        /**
         * 预案公告日
         */
        private final LocalDate dividPlanAnnounceDate;

        /**
         * 分红实施公告日
         */
        private final LocalDate dividPlanDate;

        /**
         * 股权登记告日
         */
        private LocalDate dividRegistDate;

        /**
         * 除权除息日期
         */
        private LocalDate dividOperateDate;

        /**
         * 派息日
         */
        private LocalDate dividPayDate;

        /**
         * 红股上市交易日
         */
        private LocalDate dividStockMarketDate;

        /**
         * 每股股利税前
         */
        private BigDecimal dividCashPsBeforeTax;

        /**
         * 每股股利税后
         */
        private String dividCashPsAfterTax;

        /**
         * 每股红股
         */
        private BigDecimal dividStocksPs;

        /**
         * 分红送转
         */
        private String dividCashStock;

        /**
         * 每股转增资本
         */
        private BigDecimal dividReserveToStockPs;

        public Dividend(String[] data) {
            code = data[0];
            if(data[1] != null && data[1].length() > 0) {
                dividPreNoticeDate = LocalDate.parse(data[1], DateTimeFormatter.ISO_LOCAL_DATE);
            }
            if(data[2] != null && data[2].length() > 0) {
                dividAgmPumDate = LocalDate.parse(data[2], DateTimeFormatter.ISO_LOCAL_DATE);
            }
            dividPlanAnnounceDate = LocalDate.parse(data[3], DateTimeFormatter.ISO_LOCAL_DATE);
            dividPlanDate = LocalDate.parse(data[4], DateTimeFormatter.ISO_LOCAL_DATE);
            if(data[5] != null && data[5].length() > 0) {
                dividRegistDate = LocalDate.parse(data[5], DateTimeFormatter.ISO_LOCAL_DATE);
            }
            if(data[6] != null && data[6].length() > 0) {
                dividOperateDate = LocalDate.parse(data[6], DateTimeFormatter.ISO_LOCAL_DATE);
            }
            if(data[7] != null && data[7].length() > 0) {
                dividPayDate = LocalDate.parse(data[7], DateTimeFormatter.ISO_LOCAL_DATE);
            }
            if(data[8] != null && data[8].length() > 0) {
                dividStockMarketDate = LocalDate.parse(data[8], DateTimeFormatter.ISO_LOCAL_DATE);
            }
            if(data[9] != null && data[9].length() > 0) {
                dividCashPsBeforeTax = new BigDecimal(data[9]);
            }
            dividCashPsAfterTax = data[10];
            if(data[11] != null && data[11].length() > 0) {
                dividStocksPs = new BigDecimal(data[11]);
            }
            dividCashStock = data[12];
            if(data[13] != null && data[13].length() > 0) {
                dividReserveToStockPs = new BigDecimal(data[13]);
            }
        }
    }
}
