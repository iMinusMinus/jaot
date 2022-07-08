package ml.iamwhatiam.baostock.infrastructure.rpc;

public final class ResultFactory {

    public static BaoStockResponse newInstance(String[] headerArray, String[] bodyArray) {
        BaoStockResponse result;
        switch (headerArray[1]) {
            case Constants.MESSAGE_TYPE_LOGIN_RESPONSE:
                    result = new LoginResponse(headerArray, bodyArray);
                    break;
            case Constants.MESSAGE_TYPE_QUERYALLSTOCK_RESPONSE:
                    result = new QueryAllStockResponse(headerArray, bodyArray);
                    break;
            case Constants.MESSAGE_TYPE_QUERYSTOCKBASIC_RESPONSE:
                    result = new QueryStockBasicResponse(headerArray, bodyArray);
                    break;
            case Constants.MESSAGE_TYPE_QUERYSTOCKINDUSTRY_RESPONSE:
                    result = new QueryStockIndustryResponse(headerArray, bodyArray);
                    break;
            case Constants.MESSAGE_TYPE_QUERYDIVIDENDDATA_RESPONSE:
                    result = new QueryDividendDataResponse(headerArray, bodyArray);
                    break;
            case Constants.MESSAGE_TYPE_QUERYSZ50STOCKS_RESPONSE: // pass through
            case Constants.MESSAGE_TYPE_QUERYHS300STOCKS_RESPONSE: // pass through
            case Constants.MESSAGE_TYPE_QUERYZZ500STOCKS_RESPONSE:
                    result = new QueryStockIndexResponse(headerArray, bodyArray);
                    break;
            case Constants.MESSAGE_TYPE_PROFITDATA_RESPONSE:
                    result = new QueryProfitDataResponse(headerArray, bodyArray);
                    break;
            case Constants.MESSAGE_TYPE_OPERATIONDATA_RESPONSE:
                    result = new QueryOperationDataResponse(headerArray, bodyArray);
                    break;
            case Constants.MESSAGE_TYPE_QUERYGROWTHDATA_RESPONSE:
                    result = new QueryGrowthDataResponse(headerArray, bodyArray);
                    break;
            case Constants.MESSAGE_TYPE_QUERYBALANCEDATA_RESPONSE:
                    result = new QueryBalanceDataResponse(headerArray, bodyArray);
                    break;
            case Constants.MESSAGE_TYPE_QUERYCASHFLOWDATA_RESPONSE:
                    result = new QueryCashFlowDataResponse(headerArray, bodyArray);
                    break;
            case Constants.MESSAGE_TYPE_QUERYDUPONTDATA_RESPONSE:
                    result = new QueryDupontDataResponse(headerArray, bodyArray);
                    break;
            case Constants.MESSAGE_TYPE_GETKDATAPLUS_RESPONSE:
                    result = new QueryHistoryKDataPlusResponse(headerArray, bodyArray);
                    break;
            // TODO
            default: result = new BaoStockResponse(headerArray, bodyArray);
        }

        return result;
    }
}
