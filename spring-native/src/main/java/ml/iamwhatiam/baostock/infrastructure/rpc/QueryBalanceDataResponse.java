package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

public class QueryBalanceDataResponse extends QueryFinanceDataResponse<QueryBalanceDataResponse.Balance> {

    public QueryBalanceDataResponse(String[] headerArray, String[] bodyArray) {
        super(headerArray, bodyArray);
    }

    @Override
    QueryBalanceDataResponse.Balance newObject(String[] data) {
        return new QueryBalanceDataResponse.Balance(data);
    }

    @Getter
    @ToString(callSuper = true)
    public static class Balance extends QueryFinanceDataResponse.Finance {

        /**
         * 流动比率:流动资产/流动负债
         */
        private BigDecimal currentRatio;

        /**
         * 速动比率:(流动资产-存货净额)/流动负债
         */
        private BigDecimal quickRatio;

        /**
         * 现金比率:(货币资金+交易性金融资产)/流动负债
         */
        private BigDecimal cashRatio;

        /**
         * 总负债同比增长率:(本期总负债-上年同期总负债)/上年同期中负债的绝对值*100%
         */
        private BigDecimal yoyLiability;

        /**
         * 资产负债率:负债总额/资产总额
         */
        private BigDecimal liabilityToAsset;

        /**
         * 权益乘数:资产总额/股东权益总额=1/(1-资产负债率)
         */
        private BigDecimal assetToEquity;

        public Balance(String[] data) {
            super(data);
            if(data[3] != null && data[3].length() > 0) {
                this.currentRatio = new BigDecimal(data[3]);
            }
            if(data[4] != null && data[4].length() > 0) {
                this.quickRatio = new BigDecimal(data[4]);
            }
            if(data[5] != null && data[5].length() > 0) {
                this.cashRatio = new BigDecimal(data[5]);
            }
            if(data[6] != null && data[6].length() > 0) {
                this.yoyLiability = new BigDecimal(data[6]);
            }
            if(data[7] != null && data[7].length() > 0) {
                this.liabilityToAsset = new BigDecimal(data[7]);
            }
            if(data[8] != null && data[8].length() > 0) {
                this.assetToEquity = new BigDecimal(data[8]);
            }
        }

    }
}
