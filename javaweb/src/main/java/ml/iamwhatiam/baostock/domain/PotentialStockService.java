package ml.iamwhatiam.baostock.domain;

import java.util.List;

public interface PotentialStockService {

    List<StockEntity> top(int total, int industryMax);
}
