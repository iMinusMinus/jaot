package ml.iamwhatiam.baostock.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Refer: org.springframework.data.jdbc.mybatis.MyBatisDataAccessStrategy
 */
@Mapper
public interface StockMapper {

    List<StockDataObject> findAll();

    int insert(StockDataObject entity);

    int update(StockDataObject entity);
}
