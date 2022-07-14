package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.extern.slf4j.Slf4j;
import ml.iamwhatiam.baostock.domain.IndustryEntity;
import ml.iamwhatiam.baostock.domain.StockEntity;
import ml.iamwhatiam.baostock.domain.StockRepository;
import ml.iamwhatiam.baostock.domain.StockValue;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
    private KMapper kMapper;

    @Override
    public List<IndustryEntity> load() {
        List<IndustryDataObject> industryDataObjectList = industryMapper.findAll();
        List<StockDataObject> stockDataObjectList = stockMapper.findAll();
        Map<String, StockDataObject> stockDataObjects = stockDataObjectList.stream().collect(Collectors.toMap(StockDataObject::getCode, Function.identity()));
        Map<String, KDataObject> kDataObjects = kMapper.findLatest(null, "d", 1)
                .stream().collect(Collectors.toMap(KDataObject::getCode, Function.identity()));
        Map<String, IndustryEntity> data = new HashMap<>();
        for(IndustryDataObject industryDataObject : industryDataObjectList) {
            IndustryEntity industry = data.get(industryDataObject.getIndustry());
            if(industry == null) {
                industry = convertIndustry(industryDataObject);
                data.put(industryDataObject.getIndustry(), industry);
            }
            StockDataObject stockDataObject = stockDataObjects.get(industryDataObject.getCode());
            // 退市股不再关注
            if(stockDataObject == null) {
                log.debug("stock[{},{}] not exist in table 'BAO_STOCK_BASIC'", industryDataObject.getCode(), industryDataObject.getCodeName());
                continue;
            }

            List<StockEntity> stocks = industry.getStocks();
            if(stocks == null) {
                stocks = new ArrayList<>();
                industry.setStocks(stocks);
            }
            StockEntity stock = convertStock(stockDataObject, kDataObjects.get(stockDataObject.getCode()));
            stocks.add(stock);
        }

        for(IndustryEntity industry : data.values()) {
            BigDecimal totalPe = BigDecimal.ZERO, totalPb = BigDecimal.ZERO;
            int countPe = 0, countPb = 0;
            for(StockEntity stock : industry.getStocks()) {
                if(stock.getValue() != null) {
                    if(stock.getValue().getPe() != null) {
                        totalPe = totalPe.add(stock.getValue().getPe());
                        countPe++;
                    }
                    if(stock.getValue().getPb() != null) {
                        totalPb = totalPb.add(stock.getValue().getPb());
                        countPb++;
                    }
                }
            }
            if(countPe != 0) {
                industry.setAvgPe(totalPe.divide(new BigDecimal(countPe), 2, RoundingMode.HALF_UP));
            }
            if(countPb != 0) {
                industry.setAvgPb(totalPb.divide(new BigDecimal(countPb), 2, RoundingMode.HALF_UP));
            }
        }

        return new ArrayList<>(data.values());
    }

    private IndustryEntity convertIndustry(IndustryDataObject dataObject) {
        IndustryEntity entity = new IndustryEntity();
        entity.setIndustry(dataObject.getIndustry());
        entity.setIndustryClassification(dataObject.getIndustryClassification());
        return entity;
    }

    private StockEntity convertStock(StockDataObject dataObject, KDataObject k) {
        StockEntity entity = new StockEntity();
        entity.setCode(dataObject.getCode());
        entity.setCodeName(dataObject.getCodeName());
        entity.setIpoDate(dataObject.getIpoDate());
        entity.setOutDate(dataObject.getOutDate());
        entity.setType(dataObject.getType());
        entity.setStatus(dataObject.getStatus());
        StockValue sv = new StockValue();
        sv.setPe(k.getTrailingTwelveMonthsPriceToEarningRatio());
        sv.setPb(k.getMostRecentQuarterPriceToBookRatio());
        entity.setValue(sv);
        return entity;
    }
}
