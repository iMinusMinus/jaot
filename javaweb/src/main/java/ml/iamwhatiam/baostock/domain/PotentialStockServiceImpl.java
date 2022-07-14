package ml.iamwhatiam.baostock.domain;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
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
        Collections.sort(stocks, (prev, next) ->
            prev.getValue().getPe().subtract(next.getValue().getPe()).intValue()
                    + prev.getValue().getPb().subtract(next.getValue().getPb()).intValue()
        );
        Map<String, Integer> counter = new HashMap<>();
        // 2. 股票放到排行榜（行业已超出限制数量除外）
        List<StockEntity> result = new ArrayList<>(total);
        for(int i = 0; i < stocks.size() && result.size() < total; i++) {
            StockEntity stock = stocks.get(i);
            // 亏损的去除
            if(stock.getValue().getPe().compareTo(BigDecimal.ZERO) < 0
                    || stock.getValue().getPb().compareTo(BigDecimal.ZERO) < 0) {
                continue;
            }
            IndustryEntity industry = relations.get(stock.getCode());
            if(stock.getValue().getPe().compareTo(industry.getAvgPe()) > 0
                || stock.getValue().getPb().compareTo(industry.getAvgPb()) > 0) {
                log.info("stock [{}] pe/pb [{}/{}] great than industry average [{}/{}]", stock.getCode(), stock.getValue().getPe(), stock.getValue().getPb(), industry.getAvgPe(), industry.getAvgPb());
                continue;
            }
            int currentQuantity = counter.getOrDefault(industry.getIndustry(), 0);
            if(currentQuantity < industryMax) {
                result.add(stock);
                counter.put(industry.getIndustry(), currentQuantity + 1);
            }
        }
        return result;
    }


}
