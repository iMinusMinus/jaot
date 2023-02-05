package ml.iamwhatiam.baostock.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface KMapper {

    /**
     * 查询最新数据
     * @param code 股票代码
     * @param frequency 频率
     * @param tradeStatus 交易状态
     *
     * @return 最新K线数据
     */
    List<KDataObject> findLatest(@Param("code") String code, @Param("frequency") String frequency, @Param("tradeStatus") Integer tradeStatus);

    /**
     * 股票一定时间范围内历史高地价
     * @param code 股票代码
     * @param start 最早时间
     * @return 股价极值信息
     */
    KDataObject extremum(@Param("code") String code, @Param("start") LocalDate start);

    List<KDataObject> findAll();

    int insert(KDataObject k);

//    int update(KDataObject k);

}
