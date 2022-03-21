package ml.iamwhatiam.baostock.infrastructure;

import ml.iamwhatiam.baostock.ApplicationTest;
import ml.iamwhatiam.baostock.domain.PotentialStockService;
import ml.iamwhatiam.baostock.domain.StockRepository;
import ml.iamwhatiam.baostock.infrastructure.rpc.BaoStockApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

public class AppConfigTest extends ApplicationTest {

    @Resource
    private BaoStockApi baoStockApi;

    @Resource
    private PotentialStockService potentialStockService;

    @Resource
    private StockRepository stockRepository;

    @Test
    public void testBean() {
        Assertions.assertNotNull(baoStockApi);
        Assertions.assertNotNull(potentialStockService);
        Assertions.assertNotNull(stockRepository);
    }
}
