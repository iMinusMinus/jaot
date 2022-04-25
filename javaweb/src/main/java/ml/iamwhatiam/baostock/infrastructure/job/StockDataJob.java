package ml.iamwhatiam.baostock.infrastructure.job;

import lombok.extern.slf4j.Slf4j;
import ml.iamwhatiam.baostock.domain.StockIndexType;
import ml.iamwhatiam.baostock.infrastructure.BaoStockProperties;
import ml.iamwhatiam.baostock.infrastructure.dao.FinanceDataObject;
import ml.iamwhatiam.baostock.infrastructure.dao.FinanceMapper;
import ml.iamwhatiam.baostock.infrastructure.dao.IndexMapper;
import ml.iamwhatiam.baostock.infrastructure.dao.IndustryDataObject;
import ml.iamwhatiam.baostock.infrastructure.dao.IndustryMapper;
import ml.iamwhatiam.baostock.infrastructure.dao.StockDataObject;
import ml.iamwhatiam.baostock.infrastructure.dao.StockIndexDataObject;
import ml.iamwhatiam.baostock.infrastructure.rpc.BaoStockApi;
import ml.iamwhatiam.baostock.infrastructure.rpc.BaoStockResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.Constants;
import ml.iamwhatiam.baostock.infrastructure.rpc.LoginRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.LoginResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryAllStockRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryAllStockResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryBalanceDataResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryCashFlowDataResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryDupontDataResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryFinanceDataRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryGrowthDataResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryOperationDataResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryProfitDataResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryStockBasicRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryStockBasicResponse;
import ml.iamwhatiam.baostock.infrastructure.dao.StockMapper;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryStockIndexRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryStockIndexResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryStockIndustryRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryStockIndustryResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StockDataJob {

    @Resource
    private BaoStockApi baoStockApi;

    @Resource
    private BaoStockProperties baoStockProperties;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private IndustryMapper industryMapper;

    @Resource
    private IndexMapper indexMapper;

    @Resource
    private FinanceMapper financeMapper;

    private LocalDate lastSyncDate;

    private String accessToken;

    private final int MONTHS_PER_SEASON = 3;

    @Scheduled(cron = "0 1 23 * * 1,2,3,4,5", zone = "Asia/Shanghai")
    public void updateStocks() {
        log.info("update stock basic info job start");
        // 虽然登录userId和后续访问鉴权相同，但未登录的请求会报“用户未登录”
        LoginResponse loginResult = baoStockApi.login(new LoginRequest(baoStockProperties.getUserId(), baoStockProperties.getPassword()));
        if(!handleRemoteResult(Constants.MESSAGE_TYPE_LOGIN_REQUEST, loginResult)) {
            return;
        }
        accessToken = loginResult.getUserId() != null && loginResult.getUserId().length() > 0 ? loginResult.getUserId() : baoStockProperties.getUserId();
        // 当天股市开市情况
        QueryAllStockResponse stocks = baoStockApi.queryAllStock(new QueryAllStockRequest(accessToken));
        if(!handleRemoteResult(Constants.MESSAGE_TYPE_QUERYALLSTOCK_REQUEST, stocks)) {
            return;
        }
        List<String> stockCodes = stocks.getData().stream().map(QueryAllStockResponse.Stock::getCode).collect(Collectors.toList());
        if(lastSyncDate == null || lastSyncDate.plusDays(baoStockProperties.getUpdateInterval()).isBefore(LocalDate.now())) {
            // 各股票上市、退市等信息
            mergeStockBasic(stockCodes);
            // 各股票行业信息
            mergeStockIndustryRelation();
            // 指数信息：上证50、沪深300、中证500
            List<StockIndexDataObject> indexes = indexMapper.findAll();
            log.info("stock index size: {}", indexes.size());
            for(StockIndexType indexType : StockIndexType.values()) {
                mergeStockIndex(indexType, indexes.stream().filter(t->t.getIndexType() == indexType.getValue()).collect(Collectors.toList()));
            }
            // 季频信息(正常交易且是股票，不是指数等类型时，才有季频信息)
            Map<String, LocalDate> stockDataObjects = stockMapper.findAll().stream()
                    .filter(t -> Integer.valueOf(1).equals(t.getStatus()) && Integer.valueOf(1).equals(t.getType()))
                    .collect(Collectors.toMap(StockDataObject::getCode, StockDataObject::getIpoDate));
            addSeasonReportIfNecessary(stockDataObjects);
        }
        lastSyncDate = LocalDate.now();
        log.info("update stock basic info job end");
    }

    private void addSeasonReportIfNecessary(Map<String, LocalDate> stocks) {
        Map<String, LocalDate> profitStockCodes = new HashMap<>(stocks);
        findMissingFinanceDataStock(profitStockCodes, financeMapper.findLatestProfitData());
        for(Map.Entry<String, LocalDate> stock : profitStockCodes.entrySet()) {
            LocalDate queryFrom = stock.getValue();
            while (queryFrom.isBefore(LocalDate.now())) {
                QueryProfitDataResponse response = baoStockApi.queryProfitData(new QueryFinanceDataRequest(accessToken, stock.getKey(), queryFrom, QueryFinanceDataRequest.FinanceType.PROFIT));
                queryFrom = queryFrom.plusMonths(MONTHS_PER_SEASON);
                if(!handleRemoteResult(QueryFinanceDataRequest.FinanceType.PROFIT.getMethod(), response)) {
                    continue;
                }
                for(QueryProfitDataResponse.Profit profitToAdd : response.getData()) {
                    financeMapper.insertProfitData(FinanceAssembler.convertProfit(profitToAdd));
                }
            }
        }

        Map<String, LocalDate> operationStockCodes = new HashMap<>(stocks);
        findMissingFinanceDataStock(operationStockCodes, financeMapper.findLatestOperationData());
        for(Map.Entry<String, LocalDate> stock : operationStockCodes.entrySet()) {
            LocalDate queryFrom = stock.getValue();
            while (queryFrom.isBefore(LocalDate.now())) {
                QueryOperationDataResponse response = baoStockApi.queryOperationData(new QueryFinanceDataRequest(accessToken, stock.getKey(), queryFrom, QueryFinanceDataRequest.FinanceType.OPERATION));
                queryFrom = queryFrom.plusMonths(MONTHS_PER_SEASON);
                if(!handleRemoteResult(QueryFinanceDataRequest.FinanceType.OPERATION.getMethod(), response)) {
                    continue;
                }
                for(QueryOperationDataResponse.Operation operationToAdd : response.getData()) {
                    financeMapper.insertOperationData(FinanceAssembler.convertOperation(operationToAdd));
                }
            }
        }

        Map<String, LocalDate> growthStockCodes = new HashMap<>(stocks);
        findMissingFinanceDataStock(growthStockCodes, financeMapper.findLatestGrowthData());
        for(Map.Entry<String, LocalDate> stock : growthStockCodes.entrySet()) {
            LocalDate queryFrom = stock.getValue();
            while (queryFrom.isBefore(LocalDate.now())) {
                QueryGrowthDataResponse response = baoStockApi.queryGrowthData(new QueryFinanceDataRequest(accessToken, stock.getKey(), queryFrom, QueryFinanceDataRequest.FinanceType.GROWTH));
                queryFrom = queryFrom.plusMonths(MONTHS_PER_SEASON);
                if(!handleRemoteResult(QueryFinanceDataRequest.FinanceType.GROWTH.getMethod(), response)) {
                    continue;
                }
                for(QueryGrowthDataResponse.Growth growthToAdd : response.getData()) {
                    financeMapper.insertGrowthData(FinanceAssembler.convertGrowth(growthToAdd));
                }
            }
        }

        Map<String, LocalDate> balanceStockCodes = new HashMap<>(stocks);
        findMissingFinanceDataStock(balanceStockCodes, financeMapper.findLatestBalanceData());
        for(Map.Entry<String, LocalDate> stock : balanceStockCodes.entrySet()) {
            LocalDate queryFrom = stock.getValue();
            while (queryFrom.isBefore(LocalDate.now())) {
                QueryBalanceDataResponse response = baoStockApi.queryBalanceData(new QueryFinanceDataRequest(accessToken, stock.getKey(), queryFrom, QueryFinanceDataRequest.FinanceType.BALANCE));
                queryFrom = queryFrom.plusMonths(MONTHS_PER_SEASON);
                if(!handleRemoteResult(QueryFinanceDataRequest.FinanceType.BALANCE.getMethod(), response)) {
                    continue;
                }
                for(QueryBalanceDataResponse.Balance balanceToAdd : response.getData()) {
                    financeMapper.insertBalanceData(FinanceAssembler.convertBalance(balanceToAdd));
                }
            }
        }

        Map<String, LocalDate> cashFlowStockCodes = new HashMap<>(stocks);
        findMissingFinanceDataStock(cashFlowStockCodes, financeMapper.findLatestCashFlowData());
        for(Map.Entry<String, LocalDate> stock : cashFlowStockCodes.entrySet()) {
            LocalDate queryFrom = stock.getValue();
            while (queryFrom.isBefore(LocalDate.now())) {
                QueryCashFlowDataResponse response = baoStockApi.queryCashFlowData(new QueryFinanceDataRequest(accessToken, stock.getKey(), queryFrom, QueryFinanceDataRequest.FinanceType.CASH_FLOW));
                queryFrom = queryFrom.plusMonths(MONTHS_PER_SEASON);
                if(!handleRemoteResult(QueryFinanceDataRequest.FinanceType.CASH_FLOW.getMethod(), response)) {
                    continue;
                }
                for(QueryCashFlowDataResponse.CashFlow cashFlowToAdd : response.getData()) {
                    financeMapper.insertCashFlowData(FinanceAssembler.convertCashFlow(cashFlowToAdd));
                }
            }
        }

        Map<String, LocalDate> dupontStockCodes = new HashMap<>(stocks);
        findMissingFinanceDataStock(dupontStockCodes, financeMapper.findLatestDupontData());
        for(Map.Entry<String, LocalDate> stock : dupontStockCodes.entrySet()) {
            LocalDate queryFrom = stock.getValue();
            while (queryFrom.isBefore(LocalDate.now())) {
                QueryDupontDataResponse response = baoStockApi.queryDupontData(new QueryFinanceDataRequest(accessToken, stock.getKey(), queryFrom, QueryFinanceDataRequest.FinanceType.DUPONT));
                queryFrom = queryFrom.plusMonths(MONTHS_PER_SEASON);
                if(!handleRemoteResult(QueryFinanceDataRequest.FinanceType.DUPONT.getMethod(), response)) {
                    continue;
                }
                for(QueryDupontDataResponse.Dupont dupontToAdd : response.getData()) {
                    financeMapper.insertDupontData(FinanceAssembler.convertDupont(dupontToAdd));
                }
            }
        }
    }

    private void findMissingFinanceDataStock(Map<String, LocalDate> total, List<? extends FinanceDataObject> savedFinanceData) {
        for(FinanceDataObject financeData : savedFinanceData) {
            if(financeData.getPubDate().plusMonths(MONTHS_PER_SEASON).isAfter(LocalDate.now())) {
                log.info("stock[{}] quarterly report has been published at {}", financeData.getCode(), financeData.getPubDate());
                total.remove(financeData.getCode());
                continue;
            } else {
                total.put(financeData.getCode(), financeData.getPubDate());
            }
        }
    }

    private void mergeStockIndex(StockIndexType indexType, List<StockIndexDataObject> indexes) {
        LocalDate queryFrom;
        // 指数信息为空，则初始化数据；否则从已更新日期开始
        if(indexes.isEmpty()) {
            queryFrom = indexType.getPubDate();
        } else {
            queryFrom = indexes.get(0).getUpdateDate();
        }
        if(queryFrom.plusMonths(indexType.getFrequency()).isAfter(LocalDate.now())) {
            log.info("{} last update date [{}]", indexType.getMethod(), queryFrom);
            return;
        }

        List<StockIndexDataObject> recentAdded = new ArrayList<>(indexes);
        while(queryFrom.isBefore(LocalDate.now())) {
            QueryStockIndexResponse recent = baoStockApi.queryIndexStock(new QueryStockIndexRequest(accessToken, indexType, queryFrom));
            queryFrom = queryFrom.plusMonths(indexType.getFrequency());
            if(!handleRemoteResult(indexType.getMethod(), recent)) {
                continue;
            }
            Map<String, StockIndexDataObject> old = recentAdded.stream()
                    .filter(t -> t.getExclusionDate() == null) // 存在多次纳入/退出指数
                    .collect(Collectors.toMap(StockIndexDataObject::getCode, Function.identity()));
            List<StockIndexDataObject> handled = new ArrayList<>();
            for(QueryStockIndexResponse.StockIndex stockIndex : recent.getData()) {
                StockIndexDataObject exist = old.get(stockIndex.getCode());
                if(exist == null) {
                    StockIndexDataObject index = new StockIndexDataObject();
                    index.setIndexType(indexType.getValue());
                    index.setCode(stockIndex.getCode());
                    index.setCodeName(stockIndex.getCodeName());
                    index.setUpdateDate(stockIndex.getUpdateDate());
                    index.setInclusionDate(stockIndex.getUpdateDate());
                    indexMapper.insert(index);
                    recentAdded.add(index);
                } else {
                    handled.add(exist);
                    StockIndexDataObject changed = new StockIndexDataObject();
                    changed.setId(exist.getId());
                    changed.setUpdateDate(stockIndex.getUpdateDate());
                    indexMapper.update(changed);
                }
            }
            // 1. 基础数据为空，不做任何处理; 2. 上个周期指数股数据存在，更新日期为查询日期，并从指数列表清除
            if(old.size() != handled.size()) {
                old.values().removeAll(handled);
                for(StockIndexDataObject stockIndexDataObject : old.values()) {
                    StockIndexDataObject exclude = new StockIndexDataObject();
                    exclude.setId(stockIndexDataObject.getId());
                    exclude.setExclusionDate(queryFrom.minusMonths(indexType.getFrequency())); // XXX 存在时间误差
                    exclude.setUpdateDate(queryFrom.minusMonths(indexType.getFrequency()));
                    recentAdded.remove(stockIndexDataObject);
                    stockIndexDataObject.setExclusionDate(exclude.getExclusionDate());
                    indexMapper.update(exclude);
                }
            }
        }
    }

    private void mergeStockIndustryRelation() {
        QueryStockIndustryResponse recent = baoStockApi.queryStockIndustry(new QueryStockIndustryRequest(accessToken));
        if(!handleRemoteResult(Constants.MESSAGE_TYPE_QUERYSTOCKINDUSTRY_REQUEST, recent)) {
            return;
        }
        List<IndustryDataObject> industries = industryMapper.findAll();
        log.info("stock industry relation size: {}", industries.size());
        Map<String, IndustryDataObject> old = industries.stream().collect(Collectors.toMap(IndustryDataObject::getCode, Function.identity()));
        for(QueryStockIndustryResponse.Industry industry : recent.getData()) {
            IndustryDataObject exist = old.get(industry.getCode());
            if(exist == null) {
                IndustryDataObject relation = new IndustryDataObject();
                relation.setCode(industry.getCode());
                relation.setCodeName(industry.getCodeName());
                relation.setIndustry(industry.getIndustry());
                relation.setIndustryClassification(industry.getIndustryClassification());
                relation.setUpdateDate(industry.getUpdateDate());
                industryMapper.insert(relation);
            } else {
                IndustryDataObject changed = new IndustryDataObject();
                changed.setCode(exist.getCode());
                int changeField = 0;
                if(!Objects.equals(industry.getIndustry(), exist.getIndustry())) {
                    changed.setIndustry(industry.getIndustry());
                    changeField++;
                }
                if(!Objects.equals(industry.getIndustryClassification(), exist.getIndustryClassification())) {
                    changed.setIndustryClassification(industry.getIndustryClassification());
                    changeField++;
                }
                if(changeField > 0) {
                    changed.setUpdateDate(industry.getUpdateDate());
                    industryMapper.update(changed);
                }
            }
        }
    }

    private void mergeStockBasic(List<String> stocks) {
        List<QueryStockBasicResponse.Stock> recent = new ArrayList<>();
        for(String stockCode : stocks) {
            QueryStockBasicResponse stockBasic = baoStockApi.queryStockBasic(new QueryStockBasicRequest(accessToken, stockCode));
            if(!handleRemoteResult(Constants.MESSAGE_TYPE_QUERYSTOCKBASIC_REQUEST, stockBasic)) {
                continue;
            }
            List<QueryStockBasicResponse.Stock> stockBasicResult = stockBasic.getData();
            recent.addAll(stockBasicResult);
        }
        List<StockDataObject> stockDataObjects = stockMapper.findAll();
        log.info("find stock data quantity: {}", stockDataObjects.size());
        Map<String, StockDataObject> old = stockDataObjects.stream().collect(Collectors.toMap(StockDataObject::getCode, Function.identity()));
        for(QueryStockBasicResponse.Stock item : recent) {
            StockDataObject exist = old.get(item.getCode());
            if(exist == null) {
                StockDataObject ipo = new StockDataObject();
                ipo.setCode(item.getCode());
                ipo.setCodeName(item.getCodeName());
                ipo.setIpoDate(item.getIpoDate());
                ipo.setOutDate(item.getOutDate());
                ipo.setType(item.getType());
                ipo.setStatus(item.getStatus());
                stockMapper.insert(ipo);
            } else {
                StockDataObject changed = new StockDataObject();
                changed.setCode(item.getCode());
                int changeField = 0;
                if(!Objects.equals(item.getCodeName(), exist.getCodeName())) {
                    changed.setCodeName(item.getCodeName());
                    changeField++;
                }
                if(!Objects.equals(item.getIpoDate(), exist.getIpoDate())) {
                    changed.setIpoDate(item.getIpoDate());
                    changeField++;
                }
                if(!Objects.equals(item.getOutDate(), exist.getOutDate())) {
                    changed.setOutDate(item.getOutDate());
                    changeField++;
                }
                if(!Objects.equals(item.getType(), exist.getType())) {
                    changed.setType(item.getType());
                    changeField++;
                }
                if(!Objects.equals(item.getStatus(), exist.getStatus())) {
                    changed.setStatus(item.getStatus());
                    changeField++;
                }
                if(changeField > 0) {
                    stockMapper.update(changed);
                }
            }
        }
    }

    private boolean handleRemoteResult(String methodOrMsgType, BaoStockResponse response) {
        String[] bodyArray = {Constants.BSERR_RECVSOCK_TIMEOUT, "timeout"};
        String[] headArray = {Constants.BAOSTOCK_CLIENT_VERSION, methodOrMsgType, String.valueOf(bodyArray[0].length() + bodyArray[1].length())};
        BaoStockResponse optionalRemoteResult = Optional.ofNullable(response).orElse(new BaoStockResponse(headArray, bodyArray));
        log.debug("{} result: {}", optionalRemoteResult.getMethod(), response);
        if(response == null || !response.isSuccess()) {
            log.error("{} fail: {}", optionalRemoteResult.getMsgType(), optionalRemoteResult.getErrorMsg());
            return false;
        }
        return true;
    }

}
