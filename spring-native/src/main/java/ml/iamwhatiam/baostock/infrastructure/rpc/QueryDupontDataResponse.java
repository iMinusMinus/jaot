package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

public class QueryDupontDataResponse extends QueryFinanceDataResponse<QueryDupontDataResponse.Dupont> {

    public QueryDupontDataResponse(String[] headerArray, String[] bodyArray) {
        super(headerArray, bodyArray);
    }

    @Override
    QueryDupontDataResponse.Dupont newObject(String[] data) {
        return new QueryDupontDataResponse.Dupont(data);
    }

    @Getter
    @ToString(callSuper = true)
    public static class Dupont extends QueryFinanceDataResponse.Finance {

        /**
         * 净资产收益率:归属母公司股东净利润/[(期初归属母公司股东的权益+期末归属母公司股东的权益)/2]*100%
         */
        private BigDecimal dupontRoe;

        /**
         * 权益乘数，反映企业财务杠杆效应强弱和财务风险:平均总资产/平均归属于母公司的股东权益
         */
        private BigDecimal dupontAssetToEquity;

        /**
         * 总资产周转率，反映企业资产管理效率的指标:营业总收入/[(期初资产总额+期末资产总额)/2]
         */
        private BigDecimal dupontAssetTurn;

        /**
         * 归属母公司股东的净利润/净利润，反映母公司控股子公司百分比。如果企业追加投资，扩大持股比例，则本指标会增加
         */
        private BigDecimal dupontPniToNi;

        /**
         * 净利润/营业总收入，反映企业销售获利率
         */
        private BigDecimal dupontNiToGr;

        /**
         * 净利润/利润总额，反映企业税负水平，该比值高则税负较低。净利润/利润总额=1-所得税/利润总额
         */
        private BigDecimal dupontTaxBurden;

        /**
         * 利润总额/息税前利润，反映企业利息负担，该比值高则税负较低。利润总额/息税前利润=1-利息费用/息税前利润
         */
        private BigDecimal dupontIntBurden;

        /**
         * 息税前利润/营业总收入，反映企业经营利润率，是企业经营获得的可供全体投资人（股东和债权人）分配的盈利占企业全部营收收入的百分比
         */
        private BigDecimal dupontEbitToGr;

        public Dupont(String[] data) {
            super(data);
            if(data[3] != null && data[3].length() > 0) {
                this.dupontRoe = new BigDecimal(data[3]);
            }
            if(data[4] != null && data[4].length() > 0) {
                this.dupontAssetToEquity = new BigDecimal(data[4]);
            }
            if(data[5] != null && data[5].length() > 0) {
                this.dupontAssetTurn = new BigDecimal(data[5]);
            }
            if(data[6] != null && data[6].length() > 0) {
                this.dupontPniToNi = new BigDecimal(data[6]);
            }
            if(data[7] != null && data[7].length() > 0) {
                this.dupontNiToGr = new BigDecimal(data[7]);
            }
            if(data[8] != null && data[8].length() > 0) {
                this.dupontTaxBurden = new BigDecimal(data[8]);
            }
            if(data[9] != null && data[9].length() > 0) {
                this.dupontIntBurden = new BigDecimal(data[9]);
            }
            if(data[10] != null && data[10].length() > 0) {
                this.dupontEbitToGr = new BigDecimal(data[10]);
            }
        }

    }
}
