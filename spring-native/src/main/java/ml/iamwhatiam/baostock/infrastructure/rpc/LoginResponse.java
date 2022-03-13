package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.ToString;

@ToString(callSuper = true)
public class LoginResponse extends BaoStockResponse {

    /**
     * 响应时间，格式yyyyMMddHHmmssSSS
     */
    private String day;

    public LoginResponse(String[] headerArray, String[] bodyArray) {
        super(headerArray, bodyArray);
        if(isSuccess()) {
            day = bodyArray[4];
            // bodyArray[5]
        }
    }
}
