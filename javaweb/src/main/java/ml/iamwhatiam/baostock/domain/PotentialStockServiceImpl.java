package ml.iamwhatiam.baostock.domain;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class PotentialStockServiceImpl implements PotentialStockService {

    private final StockRepository stockRepository;

    public PotentialStockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * 最具潜力股票：行业潜力（成熟产业看利润，新兴产业看营收增速）、股票的资本营收/收益优于行业均值、当前股价与历史/行业比低
     * @param total 总数量
     * @param filter 过滤条件
     * @param comparator 排序条件
     * @return
     */
    @Override
    public List<StockEntity> top(int total, Predicate<StockEntity> filter, Comparator<StockEntity> comparator) {
        List<IndustryEntity> repo = stockRepository.load();
        Map<String, IndustryEntity> relations = new HashMap<>(repo.size());
        List<StockEntity> stocks = new ArrayList<>(repo.size());
        // 1. 所有股票基本信息
        for(IndustryEntity industry : repo) {
            for(StockEntity stock : industry.getStocks()) {
                relations.put(stock.getCode(), industry);
                stocks.add(stock);
            }
        }
        int n = total;
        if(total > stocks.size()) {
            log.info("top rank exceed: N={}, stock quantity is {} ", total, stocks.size());
            n = stocks.size();
        }
        // 2. 过滤
        stocks = stocks.stream().filter(filter).collect(Collectors.toList());
        // 3. 补充信息
        for (StockEntity stock : stocks) {
            stockRepository.fillValue(stock);
        }
        // 4. 再过滤
        stocks = stocks.stream()
                .filter(t -> {
                    BigDecimal avgPe = relations.get(t.getCode()).getAvgPe();
                    BigDecimal avgPb = relations.get(t.getCode()).getAvgPb();
                    return (t.getValue().getPe().compareTo(avgPe) > 0 && t.getValue().getPb().compareTo(avgPb) > 0
                            && avgPe.compareTo(BigDecimal.ZERO) > 0 && avgPb.compareTo(BigDecimal.ZERO) > 0)
                            ||
                            (t.getValue().getPrice().divide(t.getValue().getHighPrice(), 2, RoundingMode.HALF_UP).compareTo(new BigDecimal("0.75")) < 0
                                    && t.getValue().getLowPrice().divide(t.getValue().getPrice(), 2, RoundingMode.HALF_UP).compareTo(new BigDecimal("0.75")) >= 0);
                })
                .collect(Collectors.toList());
        // 5. 排序
        Collections.sort(stocks, comparator);
        return new ArrayList<>(stocks.subList(0, n));
    }


}
