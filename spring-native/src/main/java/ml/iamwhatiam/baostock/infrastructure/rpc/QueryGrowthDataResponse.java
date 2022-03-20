package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

public class QueryGrowthDataResponse extends QueryFinanceDataResponse<QueryGrowthDataResponse.Growth> {

    public QueryGrowthDataResponse(String[] headerArray, String[] bodyArray) {
        super(headerArray, bodyArray);
    }

    @Override
    QueryGrowthDataResponse.Growth newObject(String[] data) {
        return new QueryGrowthDataResponse.Growth(data);
    }

    @Getter
    @ToString(callSuper = true)
    public static class Growth extends QueryFinanceDataResponse.Finance {

        /**
         * 净资产同比增长率:(本期净资产-上年同期净资产)/上年同期净资产的绝对值*100%
         */
        private BigDecimal yoyEquity;

        /**
         * 总资产同比增长率:(本期总资产-上年同期总资产)/上年同期总资产的绝对值*100%
         */
        private BigDecimal yoyAsset;

        /**
         * 净利润同比增长率:(本期净利润-上年同期净利润)/上年同期净利润的绝对值*100%
         */
        private BigDecimal yoyNi;

        /**
         * 基本每股收益同比增长率:(本期基本每股收益-上年同期基本每股收益)/上年同期基本每股收益的绝对值*100%
         */
        private BigDecimal yoyEpsBasic;

        /**
         * 归属母公司股东净利润同比增长率:(本期归属母公司股东净利润-上年同期归属母公司股东净利润)/上年同期归属母公司股东净利润的绝对值*100%
         */
        private BigDecimal yoyPni;

        public Growth(String[] data) {
            super(data);
            if(data[3] != null && data[3].length() > 0) {
                this.yoyEquity = new BigDecimal(data[3]);
            }
            if(data[4] != null && data[4].length() > 0) {
                this.yoyAsset = new BigDecimal(data[4]);
            }
            if(data[5] != null && data[5].length() > 0) {
                this.yoyNi = new BigDecimal(data[5]);
            }
            if(data[6] != null && data[6].length() > 0) {
                this.yoyEpsBasic = new BigDecimal(data[6]);
            }
            if(data[7] != null && data[7].length() > 0) {
                this.yoyPni = new BigDecimal(data[7]);
            }
        }
    }
}
