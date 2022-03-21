package ml.iamwhatiam.baostock.domain;

import javax.annotation.Resource;
import java.util.List;

public class PotentialStockServiceImpl implements PotentialStockService {

    @Resource
    private final StockRepository stockRepository;

    public PotentialStockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public List<IndustryEntity> top(int total, int industryMax, int industryMin) {
        // FIXME
        return stockRepository.load();
    }
}
