package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString(callSuper = true)
public class DupontDataObject extends FinanceDataObject {

    /**
     * 净资产收益率:归属母公司股东净利润/[(期初归属母公司股东的权益+期末归属母公司股东的权益)/2]*100%
     */
    private BigDecimal returnOfEquity;

    /**
     * 权益乘数，反映企业财务杠杆效应强弱和财务风险:平均总资产/平均归属于母公司的股东权益
     */
    private BigDecimal assetToEquity;

    /**
     * 总资产周转率，反映企业资产管理效率的指标:营业总收入/[(期初资产总额+期末资产总额)/2]
     */
    private BigDecimal assetTurn;

    /**
     * 归属母公司股东的净利润/净利润，反映母公司控股子公司百分比。如果企业追加投资，扩大持股比例，则本指标会增加
     */
    private BigDecimal netProfitAttributableToShareHoldersOfTheParentCompanyToNetProfit;

    /**
     * 净利润/营业总收入，反映企业销售获利率
     */
    private BigDecimal netProfitToGrossRevenue;

    /**
     * 净利润/利润总额，反映企业税负水平，该比值高则税负较低。净利润/利润总额=1-所得税/利润总额
     */
    private BigDecimal taxBurden;

    /**
     * 利润总额/息税前利润，反映企业利息负担，该比值高则税负较低。利润总额/息税前利润=1-利息费用/息税前利润
     */
    private BigDecimal intBurden;

    /**
     * 息税前利润/营业总收入，反映企业经营利润率，是企业经营获得的可供全体投资人（股东和债权人）分配的盈利占企业全部营收收入的百分比
     */
    private BigDecimal earningsBeforeInterestAndTaxToInterestToGrossRevenue;
}
