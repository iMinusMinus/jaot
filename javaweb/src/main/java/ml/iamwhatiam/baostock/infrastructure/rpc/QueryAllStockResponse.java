package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true)
public class QueryAllStockResponse extends BaoStockResponse {

    @Getter
    private int curPageNum;

    @Getter
    private int perPageCount;

    private String day;

    @Getter
    private List<Stock> data;

    private String[] fields;

    public QueryAllStockResponse(String[] headerArray, String[] bodyArray) {
        super(headerArray, bodyArray);
        if(isSuccess()) {
            curPageNum = Integer.parseInt(bodyArray[4]);
            perPageCount = Integer.parseInt(bodyArray[5]);
            List<List<String>> tmp = JsonParser.parseRecord(bodyArray[6]);
            day = bodyArray[7];
            fields = bodyArray[8].split(Constants.ATTRIBUTE_SPLIT);
            data = new ArrayList<>(tmp.size());
            for(List<String> list : tmp) {
                data.add(new Stock(list.toArray(new String[0])));
            }
        }
    }

    @ToString
    public static class Stock {

        /**
         * 证券代码
         */
        @Getter
        private final String code;

        /**
         * 交易状态(1：正常交易 0：停牌）
         */
        @Getter
        private final String tradeStatus;

        /**
         * 证券名称
         */
        @Getter
        private final String codeName;

        public Stock(String[] data) {
            code = data[0];
            tradeStatus = data[1];
            codeName = data[2];
        }
    }
}
