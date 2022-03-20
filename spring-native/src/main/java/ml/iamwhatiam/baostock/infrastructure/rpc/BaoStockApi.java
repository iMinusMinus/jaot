package ml.iamwhatiam.baostock.infrastructure.rpc;

public interface BaoStockApi {

    /**
     * 登录系统
     *
     * @param request 用户名及密码
     * @return 登录结果
     */
    LoginResponse login(LoginRequest request);

    /**
     * 证券代码查询：
     * 获取指定交易日期所有股票列表。
     * 通过API接口获取证券代码及股票交易状态信息，与日K线数据同时更新。
     * 可以通过参数‘某交易日’获取数据（包括：A股、指数），数据范围同接口query_history_k_data_plus
     * @param request 可指定查询的交易日期，为空时默认当前日期
     * @return 各证券交易状态
     */
    QueryAllStockResponse queryAllStock(QueryAllStockRequest request);

    /**
     * 证券基本资料：
     * 通过API接口获取证券基本资料，可以通过参数设置获取对应证券代码、证券名称的数据
     * @param request 可指定股票代码或股票名称（股票名称支持模糊查询）
     * @return 各股票类型、上市/退市日期及交易状态
     */
    QueryStockBasicResponse queryStockBasic(QueryStockBasicRequest request);

    /**
     * 行业分类：
     * 通过API接口获取行业分类信息，更新频率：每周一更新
     * @param request 可指定股票代码或查询日期
     * @return 行业分类信息
     */
    QueryStockIndustryResponse queryStockIndustry(QueryStockIndustryRequest request);

    /**
     * 指数（上证50/沪深300/中证500）成分股
     * 通过API接口获取上证50/沪深300/中证500成分股信息，更新频率：每周一更新
     * @param request 指数类型，可指定查询日期
     * @return 成分股信息
     */
    QueryStockIndexResponse queryIndexStock(QueryStockIndexRequest request);

    /**
     * 季频盈利能力
     * 通过API接口获取季频盈利能力信息，可以通过参数设置获取对应年份、季度数据，提供2007年至今数据
     * @param request 股票代码，可指定年份和季度
     * @return 收入及利润
     */
    QueryProfitDataResponse queryProfitData(QueryFinanceDataRequest request);

    /**
     * 季频营运能力
     * 通过API接口获取季频营运能力信息，可以通过参数设置获取对应年份、季度数据，提供2007年至今数据
     * @param request 股票代码，可指定年份和季度
     * @return 应收账款、存货、流动资金周转信息
     */
    QueryOperationDataResponse queryOperationData(QueryFinanceDataRequest request);

    /**
     * 季频成长能力
     * 通过API接口获取季频成长能力信息，可以通过参数设置获取对应年份、季度数据，提供2007年至今数据
     * @param request 股票代码，可指定年份和季度
     * @return 资产、利润增长信息
     */
    QueryGrowthDataResponse queryGrowthData(QueryFinanceDataRequest request);

    /**
     * 季频偿债能力
     * 通过API接口获取季频偿债能力信息，可以通过参数设置获取对应年份、季度数据，提供2007年至今数据
     * @param request 股票代码，可指定年份和季度
     * @return 资产、负债信息
     */
    QueryBalanceDataResponse queryBalanceData(QueryFinanceDataRequest request);

    /**
     * 季频现金流量
     * 通过API接口获取季频现金流量信息，可以通过参数设置获取对应年份、季度数据，提供2007年至今数据
     * @param request 股票代码，可指定年份和季度
     * @return 资产、现金流信息
     */
    QueryCashFlowDataResponse queryCashFlowData(QueryFinanceDataRequest request);

    /**
     * 季频杜邦指数
     * 通过API接口获取季频杜邦指数信息，可以通过参数设置获取对应年份、季度数据，提供2007年至今数据
     * @param request 股票代码，可指定年份和季度
     * @return 杜邦指数信息
     */
    QueryDupontDataResponse queryDupontData(QueryFinanceDataRequest request);

    /**
     * 获取历史A股K线数据:
     * 通过API接口获取A股历史交易数据，可以通过参数设置获取日k线、周k线、月k线，以及5分钟、15分钟、30分钟和60分钟k线数据，适合搭配均线数据进行选股和分析
     * @param request 必须指定股票代码，可指定开始日期和结束日期，可指定频率、复权类型
     * @return 股票k线数据
     */
    QueryHistoryKDataPlusResponse queryHistoryKDataPlus(QueryHistoryKDataPlusRequest request);

}
