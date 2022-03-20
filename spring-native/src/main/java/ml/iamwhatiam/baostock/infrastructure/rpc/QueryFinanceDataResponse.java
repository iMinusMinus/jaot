package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ToString
public abstract class QueryFinanceDataResponse<T> extends BaoStockResponse {

    @Getter
    protected int curPageNum;

    @Getter
    protected int perPageCount;

    @Getter
    protected List<T> data;

    @Getter
    protected String code;

    @Getter
    protected Year year;

    @Getter
    protected int quarter;

    protected String[] fields;

    public QueryFinanceDataResponse(String[] headerArray, String[] bodyArray) {
        super(headerArray, bodyArray);
        if(isSuccess()) {
            curPageNum = Integer.parseInt(bodyArray[4]);
            perPageCount = Integer.parseInt(bodyArray[5]);
            List<List<String>> tmp = JsonParser.parseRecord(bodyArray[6]);
            code = bodyArray[7];
            year = Year.parse(bodyArray[8]);
            quarter = Integer.parseInt(bodyArray[9]);
            fields = bodyArray[10].split(Constants.ATTRIBUTE_SPLIT);
            data = new ArrayList<>(tmp.size());
            for(List<String> list : tmp) {
                data.add(newObject(list.toArray(new String[0])));
            }
        }
    }

    abstract T newObject(String[] data);

    @Getter
    @ToString
    public static class Finance {

        /**
         * 证券代码
         */
        protected final String code;

        /**
         * 公司发布财报的日期
         */
        protected final LocalDate pubDate;

        /**
         * 财报统计的季度的最后一天
         */
        protected final LocalDate statDate;

        public Finance(String[] data) {
            this.code = data[0];
            this.pubDate = LocalDate.parse(data[1], DateTimeFormatter.ISO_LOCAL_DATE);
            this.statDate = LocalDate.parse(data[2], DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }
}
