package ml.iamwhatiam.baostock.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IndexMapper {

    List<StockIndexDataObject> findAll();

    int insert(StockIndexDataObject stockIndex);

    int update(StockIndexDataObject stockIndex);
}
