package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.extern.slf4j.Slf4j;
import ml.iamwhatiam.baostock.domain.IndustryEntity;
import ml.iamwhatiam.baostock.domain.Opportunity;
import ml.iamwhatiam.baostock.domain.StockEntity;
import ml.iamwhatiam.baostock.domain.StockRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class StockRepositoryImpl implements StockRepository {

    @Resource
    private StockMapper stockMapper;

    @Resource
    private IndustryMapper industryMapper;

    @Resource
    private FinanceMapper financeMapper;

    @Override
    public List<IndustryEntity> load() {
        List<IndustryDataObject> industryDataObjectList = industryMapper.findAll();
        List<StockDataObject> stockDataObjectList = stockMapper.findAll();
        Map<String, List<ProfitDataObject>> stockProfitDataObjects = financeMapper.findAllProfitData()
                .stream().collect(Collectors.groupingBy(ProfitDataObject::getCode, Collectors.toList()));
        Map<String, List<GrowthDataObject>> growthDataObjects = financeMapper.findAllGrowthData()
                .stream().collect(Collectors.groupingBy(GrowthDataObject::getCode, Collectors.toList()));
        Map<String, StockDataObject> stockDataObjects = stockDataObjectList.stream().collect(Collectors.toMap(StockDataObject::getCode, Function.identity()));
        Map<String, IndustryEntity> data = new HashMap<>();
        for(IndustryDataObject industryDataObject : industryDataObjectList) {
            IndustryEntity industry = data.get(industryDataObject.getIndustry());
            if(industry == null) {
                industry = convertIndustry(industryDataObject);
                data.put(industryDataObject.getIndustry(), industry);
            }
            // TODO
            StockDataObject stockDataObject = stockDataObjects.get(industryDataObject.getCode());
            List<StockEntity> stocks = industry.getStocks();
            if(stocks == null) {
                stocks = new ArrayList<>();
                industry.setStocks(stocks);
            }
            if(stockDataObject == null) {
                log.warn("stock[{},{}] not exist in table 'BAO_STOCK_BASIC'", industryDataObject.getCode(), industryDataObject.getCodeName());
                continue;
            }
            StockEntity stock = convertStock(stockDataObject, stockProfitDataObjects.get(stockDataObject.getCode()), growthDataObjects.get(stockDataObject.getCode()));
            // TODO
            stocks.add(stock);
        }

        for(IndustryEntity industry : data.values()) {
            int market = 0;
            int profit = 0;
            for(StockEntity stock : industry.getStocks()) {
                market += stock.getRevenue();
                profit += stock.getProfit();
            }
            industry.setMarket(market);
            industry.setProfit(profit);
        }

        return new ArrayList<>(data.values());
    }

    private IndustryEntity convertIndustry(IndustryDataObject dataObject) {
        IndustryEntity entity = new IndustryEntity();
        entity.setIndustry(dataObject.getIndustry());
        entity.setIndustryClassification(dataObject.getIndustryClassification());
        return entity;
    }

    private StockEntity convertStock(StockDataObject dataObject, List<ProfitDataObject> profitDataObjects, List<GrowthDataObject> growthDataObjects) {
        StockEntity entity = new StockEntity();
        entity.setCode(dataObject.getCode());
        entity.setCodeName(dataObject.getCodeName());
        entity.setIpoDate(dataObject.getIpoDate());
        entity.setOutDate(dataObject.getOutDate());
        entity.setType(dataObject.getType());
        entity.setStatus(dataObject.getStatus());
        if(profitDataObjects != null && !profitDataObjects.isEmpty()) {
            Collections.sort(profitDataObjects, Comparator.comparing(ProfitDataObject::getPubDate));
            ProfitDataObject recent = profitDataObjects.get(profitDataObjects.size() - 1);
            if(recent.getNetProfit() != null) {
                entity.setProfit(recent.getNetProfit().intValue());
            }
            if(recent.getPrimaryBusinessRevenue() != null) {
                entity.setRevenue(recent.getPrimaryBusinessRevenue().intValue());
            }
        }
        if(growthDataObjects != null && !growthDataObjects.isEmpty()) {
            Collections.sort(growthDataObjects, Comparator.comparing(GrowthDataObject::getPubDate));
            BigDecimal grow = growthDataObjects.get(growthDataObjects.size() - 1).getYearOverYearEarningsPerShareBasic();
            BigDecimal netGrow = growthDataObjects.get(growthDataObjects.size() - 1).getYearOverYearNetProfit();
            if((grow != null && grow.compareTo(BigDecimal.ZERO) > 0) || (netGrow != null && netGrow.compareTo(BigDecimal.ZERO) > 0)) {
                Opportunity opportunity = new Opportunity();
                if(grow != null && grow.compareTo(BigDecimal.ZERO) > 0) {
                    opportunity.setGrow(grow);
                }
                if(netGrow != null && netGrow.compareTo(BigDecimal.ZERO) > 0) {
                    opportunity.setNetGrow(netGrow);
                }
                entity.setOpportunities(Arrays.asList(opportunity));
            }
        }
        return entity;
    }
}
