package ml.iamwhatiam.baostock.infrastructure;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = BaoStockProperties.BAOSTOCK_PREFIX)
@Getter
@Setter
@ToString
public class BaoStockProperties {

    public static final String BAOSTOCK_PREFIX = "baostock";

    private String userId;

    private String password;

    private int updateInterval = 7;

    private long sessionTimeout = 60 * 60 * 1000L;
}
