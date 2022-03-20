package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
public class QueryOperationDataResponse extends QueryFinanceDataResponse<QueryOperationDataResponse.Operation> {

    public QueryOperationDataResponse(String[] headerArray, String[] bodyArray) {
        super(headerArray, bodyArray);
    }

    @Override
    QueryOperationDataResponse.Operation newObject(String[] data) {
        return new QueryOperationDataResponse.Operation(data);
    }

    @Getter
    @ToString(callSuper = true)
    public static class Operation extends QueryFinanceDataResponse.Finance{

        /**
         * 应收账款周转率(次):营业收入/[(期初应收票据及应收账款净额+期末应收票据及应收账款净额)/2]
         */
        private BigDecimal nrTurnRatio;

        /**
         * 应收账款周转天数(天):季报天数/应收账款周转率(一季报：90天，中报：180天，三季报：270天，年报：360天)
         */
        private BigDecimal nrTurnDays;

        /**
         * 存货周转率(次):营业成本/[(期初存货净额+期末存货净额)/2]
         */
        private BigDecimal invTurnRatio;

        /**
         * 存货周转天数(天):季报天数/存货周转率(一季报：90天，中报：180天，三季报：270天，年报：360天)
         */
        private BigDecimal invTurnDays;

        /**
         * 流动资产周转率(次):营业总收入/[(期初流动资产+期末流动资产)/2]
         */
        private BigDecimal caTurnRatio;

        /**
         * 总资产周转率:营业总收入/[(期初资产总额+期末资产总额)/2]
         */
        private BigDecimal assetTurnRatio;

        public Operation(String[] data) {
            super(data);
            if(data[3] != null && data[3].length() > 0) {
                this.nrTurnRatio = new BigDecimal(data[3]);
            }
            if(data[4] != null && data[4].length() > 0) {
                this.nrTurnDays = new BigDecimal(data[4]);
            }
            if(data[5] != null && data[5].length() > 0) {
                this.invTurnRatio = new BigDecimal(data[5]);
            }
            if(data[6] != null && data[6].length() > 0) {
                this.invTurnDays = new BigDecimal(data[6]);
            }
            if(data[7] != null && data[7].length() > 0) {
                this.caTurnRatio = new BigDecimal(data[7]);
            }
            if(data[8] != null && data[8].length() > 0) {
                this.assetTurnRatio = new BigDecimal(data[8]);
            }
        }
    }
}
