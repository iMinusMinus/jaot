package ml.iamwhatiam.baostock.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DividendMapper {

    List<DividendDataObject> findLatest();

    List<DividendDataObject> findAll();

    int insert(DividendDataObject dividend);

//    int update(DividendDataObject dividend);

}
