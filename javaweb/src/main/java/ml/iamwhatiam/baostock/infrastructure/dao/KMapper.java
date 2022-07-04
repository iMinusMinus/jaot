package ml.iamwhatiam.baostock.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KMapper {

    List<KDataObject> findLatest();

    List<KDataObject> findAll();

    int insert(KDataObject k);

//    int update(KDataObject k);

}
