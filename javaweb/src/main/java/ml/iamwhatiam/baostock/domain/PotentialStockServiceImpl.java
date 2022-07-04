package ml.iamwhatiam.baostock.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PotentialStockServiceImpl implements PotentialStockService {

    private final StockRepository stockRepository;

    public PotentialStockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * 最具潜力股票：行业潜力（成熟产业看利润，新兴产业看营收增速）、股票的资本营收/收益优于行业均值
     * @param total
     * @param industryMax
     * @return
     */
    @Override
    public List<StockEntity> top(int total, int industryMax) {
        List<IndustryEntity> repo = stockRepository.load();
        Map<String, IndustryEntity> relations = new HashMap<>(repo.size());
        List<StockEntity> stocks = new ArrayList<>(repo.size());
        // 1. 所有股票排名
        for(IndustryEntity industry : repo) {
            for(StockEntity stock : industry.getStocks()) {
                relations.put(stock.getCode(), industry);
                stocks.add(stock);
            }
        }
        if(total > stocks.size()) {
            log.info("top rank exceed: N={}, stock quantity is {} ", total, stocks.size());
        }
        Collections.sort(stocks, (prev, next) -> {
            if(prev.getProfit() != next.getProfit()) {
                return prev.getProfit() - next.getProfit();
            }
            if(prev.getRevenue() != next.getRevenue()) {
                return prev.getRevenue() - next.getRevenue();
            }
            if(prev.getOpportunities() != null && next.getOpportunities() == null) {
                return 1;
            }
            if(prev.getOpportunities() == null && next.getOpportunities() != null) {
                return -1;
            }
            if(prev.getOpportunities() != null && next.getOpportunities() != null) {
                return prev.getOpportunities().get(0).getGrow().subtract(next.getOpportunities().get(0).getGrow()).intValue();
            }
            if(prev.getRisks() != null && next.getRisks() == null) {
                return -1;
            }
            if(prev.getRisks() == null && next.getRisks() != null) {
                return 1;
            }
            return 0;
        });
        Map<String, Integer> counter = new HashMap<>();
        // 2. 股票放到排行榜（行业已超出限制数量除外）
        List<StockEntity> result = new ArrayList<>(total);
        for(int i = stocks.size() - 1; i > 0 && result.size() < total; i--) {
            StockEntity stock = stocks.get(i);
            IndustryEntity industry = relations.get(stock.getCode());
            int currentQuantity = counter.getOrDefault(industry.getIndustry(), 0);
            if(currentQuantity < industryMax) {
                result.add(stock);
            }
            counter.put(industry.getIndustry(), currentQuantity + 1);
        }
        return result;
    }


}
