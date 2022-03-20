package ml.iamwhatiam.baostock.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 财务数据
 */
@Mapper
public interface FinanceMapper {

    int insertProfitData(ProfitDataObject profit);

    List<ProfitDataObject> findAllProfitData();

    List<ProfitDataObject> findLatestProfitData();

    int insertOperationData(OperationDataObject operation);

    List<OperationDataObject> findAllOperationData();

    List<OperationDataObject> findLatestOperationData();

    int insertGrowthData(GrowthDataObject growth);

    List<GrowthDataObject> findAllGrowthData();

    List<GrowthDataObject> findLatestGrowthData();

    int insertBalanceData(BalanceDataObject balance);

    List<BalanceDataObject> findAllBalanceData();

    List<BalanceDataObject> findLatestBalanceData();

    int insertCashFlowData(CashFlowDataObject cashFlow);

    List<CashFlowDataObject> findAllCashFlowData();

    List<CashFlowDataObject> findLatestCashFlowData();

    int insertDupontData(DupontDataObject dupont);

    List<DupontDataObject> findAllDupontData();

    List<DupontDataObject> findLatestDupontData();
}
