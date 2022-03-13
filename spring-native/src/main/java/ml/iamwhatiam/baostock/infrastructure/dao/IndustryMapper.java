package ml.iamwhatiam.baostock.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IndustryMapper {

    List<IndustryDataObject> findAll();

    int insert(IndustryDataObject industry);

    int update(IndustryDataObject industry);
}
