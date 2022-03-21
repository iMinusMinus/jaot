package ml.iamwhatiam.baostock.domain;

import java.util.List;

public interface StockRepository {

    List<IndustryEntity> load();
}
