package ml.iamwhatiam.baostock.infrastructure.rpc;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoginRequest extends BaoStockRequest {

    private final String password;

    private final int options;

    public LoginRequest(String userId, String password) {
        this(userId, password, 0);
    }

    public LoginRequest(String userId, String password, int options) {
        super(userId);
        this.password = password;
        this.options = options;
    }

    @Override
    public String getRequestCode() {
        return Constants.MESSAGE_TYPE_LOGIN_REQUEST;
    }

    @Override
    public String encode() {
        return "login" + Constants.MESSAGE_SPLIT + userId + Constants.MESSAGE_SPLIT + password + Constants.MESSAGE_SPLIT + options;
    }
}
