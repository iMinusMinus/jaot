package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.extern.slf4j.Slf4j;
import ml.iamwhatiam.baostock.domain.IndustryEntity;
import ml.iamwhatiam.baostock.domain.StockEntity;
import ml.iamwhatiam.baostock.domain.StockRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
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

    @Override
    public List<IndustryEntity> load() {
        List<IndustryDataObject> industryDataObjectList = industryMapper.findAll();
        List<StockDataObject> stockDataObjectList = stockMapper.findAll();
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
            StockEntity stock = convertStock(stockDataObject);
            // TODO
            stocks.add(stock);
        }
        return new ArrayList<>(data.values());
    }

    private IndustryEntity convertIndustry(IndustryDataObject dataObject) {
        IndustryEntity entity = new IndustryEntity();
        entity.setIndustry(dataObject.getIndustry());
        entity.setIndustryClassification(dataObject.getIndustryClassification());
        return entity;
    }

    private StockEntity convertStock(StockDataObject dataObject) {
        StockEntity entity = new StockEntity();
        entity.setCode(dataObject.getCode());
        entity.setCodeName(dataObject.getCodeName());
        entity.setIpoDate(dataObject.getIpoDate());
        entity.setOutDate(dataObject.getOutDate());
        entity.setType(dataObject.getType());
        entity.setStatus(dataObject.getStatus());
        return entity;
    }
}
