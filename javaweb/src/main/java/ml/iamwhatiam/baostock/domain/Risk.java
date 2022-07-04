package ml.iamwhatiam.baostock.domain;

/**
 * 股价下跌风险：
 * <ul>
 *     <li>财务造假（存货|存货周转率、现金流|利息、预付账款|交易对手）：獐子岛于2014、2018、2019、2020多次出现“扇贝跑路”</li>
 *     <li>自然灾害：2020年开始爆发的新冠（COVID-19）疫情，旅游业营收下降，制药业营收增加</li>
 *     <li>政策影响：2020年运营的P2P机构清零，P2P行业在国内不复存在</li>
 * </ul>
 */
public class Risk extends AbstractValueObject {

    private int effect;
}
