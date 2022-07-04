package ml.iamwhatiam.baostock.infrastructure.web;

import ml.iamwhatiam.baostock.domain.StockEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO use mapstruct or selma
public abstract class StockAssembler {

    public static List<StockVO> convert(List<StockEntity> data) {
        if(data == null || data.isEmpty()) {
            return Collections.emptyList();
        }
        List<StockVO> transformed = new ArrayList<>(data.size());
        for(StockEntity stockEntity : data) {
            StockVO stock = new StockVO();
            stock.setCode(stockEntity.getCode());
            stock.setCodeName(stockEntity.getCodeName());
//            stock.setIndustry(item.getIndustry());
            if(stockEntity.getValue() != null) {
                stock.setPe(stockEntity.getValue().getPe());
                stock.setPb(stockEntity.getValue().getPb());
                stock.setPd(stockEntity.getValue().getPd());
                stock.setPr(stockEntity.getValue().getPr());
            }
            transformed.add(stock);
        }
        return transformed;
    }
}
