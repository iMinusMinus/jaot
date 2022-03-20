package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

public class QueryCashFlowDataResponse extends QueryFinanceDataResponse<QueryCashFlowDataResponse.CashFlow> {

    public QueryCashFlowDataResponse(String[] headArray, String[] bodyArray) {
        super(headArray, bodyArray);
    }

    @Override
    QueryCashFlowDataResponse.CashFlow newObject(String[] data) {
        return new QueryCashFlowDataResponse.CashFlow(data);
    }

    @Getter
    @ToString(callSuper = true)
    public static class CashFlow extends QueryFinanceDataResponse.Finance {

        /**
         * 流动资产除以总资产
         */
        private BigDecimal caToAsset;

        /**
         * 非流动资产除以总资产
         */
        private BigDecimal ncaToAsset;

        /**
         * 有形资产除以总资产
         */
        private BigDecimal tangibleAssetToAsset;

        /**
         * 已获利息倍数:息税前利润/利息费用
         */
        private BigDecimal ebitToInterest;

        /**
         * 经营活动产生的现金流量净额除以营业收入
         */
        private BigDecimal cfoToOr;

        /**
         * 经营性现金净流量除以净利润
         */
        private BigDecimal cfoToNp;

        /**
         * 经营性现金净流量除以营业总收入
         */
        private BigDecimal cfoToGr;

        public CashFlow(String[] data) {
            super(data);
            if(data[3] != null && data[3].length() > 0) {
                this.caToAsset = new BigDecimal(data[3]);
            }
            if(data[4] != null && data[4].length() > 0) {
                this.ncaToAsset = new BigDecimal(data[4]);
            }
            if(data[5] != null && data[5].length() > 0) {
                this.tangibleAssetToAsset = new BigDecimal(data[5]);
            }
            if(data[6] != null && data[6].length() > 0) {
                this.ebitToInterest = new BigDecimal(data[6]);
            }
            if(data[7] != null && data[7].length() > 0) {
                this.cfoToOr = new BigDecimal(data[7]);
            }
            if(data[8] != null && data[8].length() > 0) {
                this.cfoToNp = new BigDecimal(data[8]);
            }
            if(data[9] != null && data[9].length() > 0) {
                this.cfoToGr = new BigDecimal(data[9]);
            }
        }

    }
}
