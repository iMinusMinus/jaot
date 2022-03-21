package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ToString
public class QueryStockIndexResponse extends BaoStockResponse {

    @Getter
    private int curPageNum;

    @Getter
    private int perPageCount;

    @Getter
    private List<QueryStockIndexResponse.StockIndex> data;

    private LocalDate date;

    private String[] fields;

    public QueryStockIndexResponse(String[] headerArray, String[] bodyArray) {
        super(headerArray, bodyArray);
        if(isSuccess()) {
            curPageNum = Integer.parseInt(bodyArray[4]);
            perPageCount = Integer.parseInt(bodyArray[5]);
            List<List<String>> tmp = JsonParser.parseRecord(bodyArray[6]);
            if(bodyArray[7] != null && bodyArray[7].length() > 0) {
                date = LocalDate.parse(bodyArray[7], DateTimeFormatter.ISO_LOCAL_DATE);
            }
            fields = bodyArray[8].split(Constants.ATTRIBUTE_SPLIT);
            data = new ArrayList<>(tmp.size());
            for(List<String> list : tmp) {
                data.add(new QueryStockIndexResponse.StockIndex(list.toArray(new String[0])));
            }
        }
    }

    @Getter
    @ToString
    public static class StockIndex {

        /**
         * 更新日期
         */
        private LocalDate updateDate;

        /**
         * 证券代码
         */
        private final String code;

        /**
         * 证券名称
         */
        private final String codeName;

        public StockIndex(String[] data) {
            if(data[0] != null && data[0].length() > 0) {
                updateDate = LocalDate.parse(data[0], DateTimeFormatter.ISO_LOCAL_DATE);
            }
            code = data[1];
            codeName = data[2];
        }
    }

}
