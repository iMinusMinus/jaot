package ml.iamwhatiam.baostock.infrastructure.job;

import ml.iamwhatiam.baostock.infrastructure.dao.BalanceDataObject;
import ml.iamwhatiam.baostock.infrastructure.dao.CashFlowDataObject;
import ml.iamwhatiam.baostock.infrastructure.dao.DupontDataObject;
import ml.iamwhatiam.baostock.infrastructure.dao.FinanceDataObject;
import ml.iamwhatiam.baostock.infrastructure.dao.GrowthDataObject;
import ml.iamwhatiam.baostock.infrastructure.dao.OperationDataObject;
import ml.iamwhatiam.baostock.infrastructure.dao.ProfitDataObject;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryBalanceDataResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryCashFlowDataResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryDupontDataResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryFinanceDataResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryGrowthDataResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryOperationDataResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryProfitDataResponse;

public abstract class FinanceAssembler {

    public static ProfitDataObject convertProfit(QueryProfitDataResponse.Profit profitToAdd) {
        if(profitToAdd == null) {
            return null;
        }
        ProfitDataObject profitReport = new ProfitDataObject();
        convert(profitToAdd, profitReport);
        profitReport.setRoeAvg(profitToAdd.getRoeAvg());
        profitReport.setNetProfitMargin(profitToAdd.getNpMargin());
        profitReport.setGrossProfitMargin(profitToAdd.getGpMargin());
        profitReport.setNetProfit(profitToAdd.getNetProfit());
        profitReport.setEarningsPerShareTimeToMarket(profitToAdd.getEpsTTM());
        profitReport.setPrimaryBusinessRevenue(profitToAdd.getMbRevenue());
        profitReport.setTotalShare(profitToAdd.getTotalShare());
        profitReport.setTradableShare(profitToAdd.getLiqaShare());
        return profitReport;
    }

    public static OperationDataObject convertOperation(QueryOperationDataResponse.Operation operationToAdd) {
        if(operationToAdd == null) {
            return null;
        }
        OperationDataObject operationReport = new OperationDataObject();
        convert(operationToAdd, operationReport);
        operationReport.setNoteReceiveTurnRatio(operationToAdd.getNrTurnRatio());
        operationReport.setNoteReceiveTurnDays(operationToAdd.getNrTurnDays());
        operationReport.setInventoryTurnRatio(operationToAdd.getInvTurnRatio());
        operationReport.setInventoryTurnDays(operationToAdd.getInvTurnDays());
        operationReport.setCurrentAssetTurnRatio(operationToAdd.getCaTurnRatio());
        operationReport.setAssetTurnRatio(operationToAdd.getAssetTurnRatio());
        return operationReport;
    }

    public static GrowthDataObject convertGrowth(QueryGrowthDataResponse.Growth growthToAdd) {
        if(growthToAdd == null) {
            return null;
        }
        GrowthDataObject growthReport = new GrowthDataObject();
        convert(growthToAdd, growthReport);
        growthReport.setYearOverYearEquity(growthToAdd.getYoyEquity());
        growthReport.setYearOverYearAsset(growthToAdd.getYoyAsset());
        growthReport.setYearOverYearNetProfit(growthToAdd.getYoyNi());
        growthReport.setYearOverYearEarningsPerShareBasic(growthToAdd.getYoyEpsBasic());
        growthReport.setYearOverYearNetProfitAttributableToShareHoldersOfTheParentCompany(growthToAdd.getYoyPni());
        return growthReport;
    }

    public static BalanceDataObject convertBalance(QueryBalanceDataResponse.Balance balanceToAdd) {
        if(balanceToAdd == null) {
            return null;
        }
        BalanceDataObject balanceReport = new BalanceDataObject();
        convert(balanceToAdd, balanceReport);
        balanceReport.setCurrentRatio(balanceToAdd.getCurrentRatio());
        balanceReport.setQuickRatio(balanceToAdd.getQuickRatio());
        balanceReport.setCashRatio(balanceToAdd.getCashRatio());
        balanceReport.setYearOverYearLiability(balanceToAdd.getYoyLiability());
        balanceReport.setLiabilityToAsset(balanceToAdd.getLiabilityToAsset());
        balanceReport.setAssetToEquity(balanceToAdd.getAssetToEquity());
        return balanceReport;
    }

    public static CashFlowDataObject convertCashFlow(QueryCashFlowDataResponse.CashFlow cashFlowToAdd) {
        if(cashFlowToAdd == null) {
            return null;
        }
        CashFlowDataObject cashFlowReport = new CashFlowDataObject();
        convert(cashFlowToAdd, cashFlowReport);
        cashFlowReport.setCurrentAssetToAsset(cashFlowToAdd.getCaToAsset());
        cashFlowReport.setNonCurrentAssetToAsset(cashFlowToAdd.getNcaToAsset());
        cashFlowReport.setTangibleAssetToAsset(cashFlowToAdd.getTangibleAssetToAsset());
        cashFlowReport.setEarningsBeforeInterestAndTaxToInterest(cashFlowToAdd.getEbitToInterest());
        cashFlowReport.setOperationalCashFlowToOperationalRevenue(cashFlowToAdd.getCfoToOr());
        cashFlowReport.setOperationalCashFlowToNetProfit(cashFlowToAdd.getCfoToNp());
        cashFlowReport.setOperationalCashFlowToGrossRevenue(cashFlowToAdd.getCfoToGr());
        return cashFlowReport;
    }

    public static DupontDataObject convertDupont(QueryDupontDataResponse.Dupont dupontToAdd) {
        if(dupontToAdd == null) {
            return null;
        }
        DupontDataObject dupontReport = new DupontDataObject();
        convert(dupontToAdd, dupontReport);
        dupontReport.setReturnOfEquity(dupontToAdd.getDupontRoe());
        dupontReport.setAssetToEquity(dupontToAdd.getDupontAssetToEquity());
        dupontReport.setAssetTurn(dupontToAdd.getDupontAssetTurn());
        dupontReport.setNetProfitAttributableToShareHoldersOfTheParentCompanyToNetProfit(dupontToAdd.getDupontPniToNi());
        dupontReport.setNetProfitToGrossRevenue(dupontToAdd.getDupontNiToGr());
        dupontReport.setTaxBurden(dupontToAdd.getDupontTaxBurden());
        dupontReport.setIntBurden(dupontToAdd.getDupontIntBurden());
        dupontReport.setEarningsBeforeInterestAndTaxToInterestToGrossRevenue(dupontToAdd.getDupontEbitToGr());
        return dupontReport;
    }

    private static void convert(QueryFinanceDataResponse.Finance financeDto, FinanceDataObject financeDataObject) {
        financeDataObject.setCode(financeDto.getCode());
        financeDataObject.setPubDate(financeDto.getPubDate());
        financeDataObject.setStatDate(financeDto.getStatDate());
    }
}
