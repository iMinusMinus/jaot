package ml.iamwhatiam.baostock.domain;

import java.util.List;

public interface PotentialStockService {

    List<IndustryEntity> top(int total, int industryMax, int industryMin);
}
