package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

public class QueryProfitDataResponse extends QueryFinanceDataResponse<QueryProfitDataResponse.Profit> {

    public QueryProfitDataResponse(String[] headerArray, String[] bodyArray) {
        super(headerArray, bodyArray);
    }

    @Override
    QueryProfitDataResponse.Profit newObject(String[] data) {
        return new QueryProfitDataResponse.Profit(data);
    }

    @Getter
    @ToString(callSuper = true)
    public static class Profit extends QueryFinanceDataResponse.Finance{

        /**
         * 净资产收益率(平均):归属母公司股东净利润/[(期初归属母公司股东的权益+期末归属母公司股东的权益)/2]*100%
         */
        private BigDecimal roeAvg;

        /**
         * 销售净利率(%):净利润/营业收入*100%
         */
        private BigDecimal npMargin;

        /**
         * 销售毛利率(%):毛利/营业收入*100%=(营业收入-营业成本)/营业收入*100%
         */
        private BigDecimal gpMargin;

        /**
         * 净利润(元)
         */
        private BigDecimal netProfit;

        /**
         * 每股收益
         */
        private BigDecimal epsTTM;

        /**
         * 主营营业收入(元)
         */
        private BigDecimal mbRevenue;

        /**
         * 总股本
         */
        private BigDecimal totalShare;

        /**
         * 流通股本
         */
        private BigDecimal liqaShare;

        public Profit(String[] data) {
            super(data);
            if(data[3] != null && data[3].length() > 0) {
                this.roeAvg = new BigDecimal(data[3]);
            }
            if(data[4] != null && data[4].length() > 0) {
                this.npMargin = new BigDecimal(data[4]);
            }
            if(data[5] != null && data[5].length() > 0) {
                gpMargin = new BigDecimal(data[5]);
            }
            if(data[6] != null && data[6].length() >0) {
                this.netProfit = new BigDecimal(data[6]);
            }
            if(data[7] != null && data[7].length() > 0) {
                this.epsTTM = new BigDecimal(data[7]);
            }
            if(data[8] != null && data[8].length() > 0) {
                this.mbRevenue = new BigDecimal(data[8]);
            }
            if(data[9] != null && data[9].length() > 0) {
                this.totalShare = new BigDecimal(data[9]);
            }
            if(data[10] != null && data[10].length() > 0) {
                this.liqaShare = new BigDecimal(data[10]);
            }
        }
    }
}
