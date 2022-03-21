package ml.iamwhatiam.baostock.infrastructure;

import ml.iamwhatiam.baostock.domain.PotentialStockService;
import ml.iamwhatiam.baostock.domain.PotentialStockServiceImpl;
import ml.iamwhatiam.baostock.domain.StockRepository;
import ml.iamwhatiam.baostock.infrastructure.rpc.BaoStockApi;
import ml.iamwhatiam.baostock.infrastructure.rpc.BaoStockRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.Constants;
import ml.iamwhatiam.baostock.infrastructure.rpc.NettyClient;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AppConfig {

    @Bean
    public BaoStockApi baoStockApi() {
        NettyClient client = new NettyClient(Constants.BAOSTOCK_SERVER_IP, Constants.BAOSTOCK_SERVER_PORT, false);
        Class[] interfaces = {BaoStockApi.class};
        return (BaoStockApi) Proxy.newProxyInstance(AppConfig.class.getClassLoader(), interfaces,
                (obj, method, args)-> client.request((BaoStockRequest) args[0]));
    }

    @Bean
    public PotentialStockService potentialStockService(StockRepository stockRepository) {
        return new PotentialStockServiceImpl(stockRepository);
    }

}
