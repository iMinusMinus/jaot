package ml.iamwhatiam.baostock.infrastructure;

import ml.iamwhatiam.baostock.domain.PotentialStockService;
import ml.iamwhatiam.baostock.domain.PotentialStockServiceImpl;
import ml.iamwhatiam.baostock.domain.StockRepository;
import ml.iamwhatiam.baostock.infrastructure.rpc.BaoStockApi;
import ml.iamwhatiam.baostock.infrastructure.rpc.BaoStockRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.Constants;
import ml.iamwhatiam.baostock.infrastructure.rpc.NettyClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = {BaoStockProperties.class})
public class AppConfig {

    private final BaoStockProperties baoStockProperties;

    public AppConfig(BaoStockProperties baoStockProperties) {
        this.baoStockProperties = baoStockProperties;
    }

    @Bean
    public BaoStockApi baoStockApi() {
        NettyClient client = new NettyClient(Constants.BAOSTOCK_SERVER_IP, Constants.BAOSTOCK_SERVER_PORT, false, baoStockProperties.getSharedConnections());
        Class[] interfaces = {BaoStockApi.class};
        return (BaoStockApi) Proxy.newProxyInstance(AppConfig.class.getClassLoader(), interfaces,
                (obj, method, args)-> client.request((BaoStockRequest) args[0]));
    }

    @Bean
    public PotentialStockService potentialStockService(StockRepository stockRepository) {
        return new PotentialStockServiceImpl(stockRepository);
    }

}
