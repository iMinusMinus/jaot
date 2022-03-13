package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true)
public class QueryStockBasicResponse extends BaoStockResponse {

    @Getter
    private int curPageNum;

    @Getter
    private int perPageCount;

    @Getter
    private String code;

    @Getter
    private String codeName;

    @Getter
    private List<QueryStockBasicResponse.Stock> data;

    private String[] fields;

    public QueryStockBasicResponse(String[] headerArray, String[] bodyArray) {
        super(headerArray, bodyArray);
        if(isSuccess()) {
            curPageNum = Integer.parseInt(bodyArray[4]);
            perPageCount = Integer.parseInt(bodyArray[5]);
            List<List<String>> tmp = JsonParser.parseRecord(bodyArray[6]);
            code = bodyArray[7];
            codeName = bodyArray[8];
            fields = bodyArray[9].split(Constants.ATTRIBUTE_SPLIT);
            data = new ArrayList<>(tmp.size());
            for(List<String> list : tmp) {
                data.add(new QueryStockBasicResponse.Stock(list.toArray(new String[0])));
            }
        }
    }

    @Getter
    public static class Stock {

        /**
         * 证券代码
         */
        private final String code;

        /**
         * 证券名称
         */
        private final String codeName;

        /**
         * 上市日期
         */
        private final LocalDate ipoDate;

        /**
         * 退市日期
         */
        private final LocalDate outDate;

        /**
         * 证券类型，其中1：股票，2：指数，3：其它，4：可转债，5：ETF
         */
        private final int type;

        /**
         * 上市状态，其中1：上市，0：退市
         */
        private final int status;

        public Stock(String[] data) {
            code = data[0];
            codeName = data[1];
            if(data[2] != null && data[2].length() > 0) {
                ipoDate = LocalDate.parse(data[2], DateTimeFormatter.ISO_LOCAL_DATE);
            } else {
                ipoDate = null;
            }
            if(data[3] != null && data[3].length() > 0) {
                outDate = LocalDate.parse(data[3], DateTimeFormatter.ISO_LOCAL_DATE);
            } else {
                outDate = null;
            }
            type = Integer.parseInt(data[4]);
            status = Integer.parseInt(data[5]);
        }
    }
}
