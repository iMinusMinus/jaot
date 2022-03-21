package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true)
public class QueryStockIndustryResponse extends BaoStockResponse {

    @Getter
    private int curPageNum;

    @Getter
    private int perPageCount;

    @Getter
    private String code;

    @Getter
    private LocalDate date;

    @Getter
    private List<QueryStockIndustryResponse.Industry> data;

    private String[] fields;

    public QueryStockIndustryResponse(String[] headerArray, String[] bodyArray) {
        super(headerArray, bodyArray);
        if(isSuccess()) {
            curPageNum = Integer.parseInt(bodyArray[4]);
            perPageCount = Integer.parseInt(bodyArray[5]);
            List<List<String>> tmp = JsonParser.parseRecord(bodyArray[6]);
            code = bodyArray[7];
            if(bodyArray[8] != null && bodyArray[8].length() > 0) {
                date = LocalDate.parse(bodyArray[8], DateTimeFormatter.ISO_LOCAL_DATE);
            }
            fields = bodyArray[9].split(Constants.ATTRIBUTE_SPLIT);
            data = new ArrayList<>(tmp.size());
            for(List<String> list : tmp) {
                data.add(new QueryStockIndustryResponse.Industry(list.toArray(new String[0])));
            }
        }
    }

    @Getter
    @ToString
    public static class Industry {

        /**
         * 更新日期
         */
        private final LocalDate updateDate;

        /**
         * 证券代码
         */
        private final String code;

        /**
         * 证券名称
         */
        private final String codeName;

        /**
         * 所属行业
         */
        private final String industry;

        /**
         * 所属行业类别
         */
        private final String industryClassification;

        public Industry(String[] data) {
            updateDate = LocalDate.parse(data[0], DateTimeFormatter.ISO_LOCAL_DATE);
            code = data[1];
            codeName = data[2];
            industry = data[3];
            industryClassification = data[4];
        }
    }
}
