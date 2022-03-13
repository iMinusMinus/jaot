package ml.iamwhatiam.baostock.infrastructure.rpc;

public final class ResultFactory {

    public static BaoStockResponse newInstance(String[] headerArray, String[] bodyArray) {
        BaoStockResponse result;
        switch (headerArray[1]) {
            case Constants.MESSAGE_TYPE_LOGIN_RESPONSE ->
                    result = new LoginResponse(headerArray, bodyArray);
            case Constants.MESSAGE_TYPE_QUERYALLSTOCK_RESPONSE ->
                    result = new QueryAllStockResponse(headerArray, bodyArray);
            case Constants.MESSAGE_TYPE_QUERYSTOCKBASIC_RESPONSE ->
                    result = new QueryStockBasicResponse(headerArray, bodyArray);
            case Constants.MESSAGE_TYPE_QUERYSTOCKINDUSTRY_RESPONSE ->
                    result = new QueryStockIndustryResponse(headerArray, bodyArray);

            case Constants.MESSAGE_TYPE_GETKDATAPLUS_RESPONSE ->
                    result = new QueryHistoryKDataPlusResponse(headerArray, bodyArray);
            // TODO
            default -> result = new BaoStockResponse(headerArray, bodyArray);
        }

        return result;
    }
}
