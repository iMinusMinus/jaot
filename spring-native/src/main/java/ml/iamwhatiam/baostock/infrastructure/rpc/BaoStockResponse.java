package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
public class BaoStockResponse implements Serializable {

    Long id;

    protected String version;

    /**
     * 错误代码，当为“0”时表示成功，当为非0时表示失败
     */
    @Getter
    @Setter
    protected String errorCode;

    /**
     * 错误信息，对错误的详细解释
     */
    @Getter
    @Setter
    protected String errorMsg;

    @Getter
    @Setter
    protected int msgBodyLength;

    @Getter
    @Setter
    protected String msgType;

    @Getter
    @Setter
    protected String method;

    @Getter
    @Setter
    protected String userId;

    public BaoStockResponse(String[] headerArray, String[] bodyArray) {
        version = headerArray[0];
        msgType = headerArray[1];
        msgBodyLength = Integer.parseInt(headerArray[2]);
        errorCode = bodyArray[0];
        errorMsg = bodyArray[1];
        if(isSuccess()) {
            method = bodyArray[2];
            userId = bodyArray[3];
        }
    }

    public boolean isSuccess() {
        return Constants.BSERR_SUCCESS.equals(errorCode);
    }
}
