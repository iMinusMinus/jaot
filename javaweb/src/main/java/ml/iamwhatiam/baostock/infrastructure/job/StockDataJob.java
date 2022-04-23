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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
        if(lastSyncDate == null || lastSyncDate.plusDays(baoStockProperties.getUpdateInterval()).compareTo(LocalDate.now()) < 0) {
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
            // 季频信息
            Map<String, LocalDate> stockDataObjects = stockMapper.findAll().stream().collect(Collectors.toMap(k -> k.getCode(), v -> v.getIpoDate()));
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
                if(!handleRemoteResult(QueryFinanceDataRequest.FinanceType.PROFIT.getMethod(), response)) {
                    continue;
                }
                for(QueryProfitDataResponse.Profit profitToAdd : response.getData()) {
                    financeMapper.insertProfitData(FinanceAssembler.convertProfit(profitToAdd));
                }
                queryFrom = queryFrom.plusMonths(MONTHS_PER_SEASON);
            }
        }

        Map<String, LocalDate> operationStockCodes = new HashMap<>(stocks);
        findMissingFinanceDataStock(operationStockCodes, financeMapper.findLatestOperationData());
        for(Map.Entry<String, LocalDate> stock : operationStockCodes.entrySet()) {
            LocalDate queryFrom = stock.getValue();
            while (queryFrom.isBefore(LocalDate.now())) {
                QueryOperationDataResponse response = baoStockApi.queryOperationData(new QueryFinanceDataRequest(accessToken, stock.getKey(), queryFrom, QueryFinanceDataRequest.FinanceType.OPERATION));
                if(!handleRemoteResult(QueryFinanceDataRequest.FinanceType.OPERATION.getMethod(), response)) {
                    continue;
                }
                for(QueryOperationDataResponse.Operation operationToAdd : response.getData()) {
                    financeMapper.insertOperationData(FinanceAssembler.convertOperation(operationToAdd));
                }
                queryFrom = queryFrom.plusMonths(MONTHS_PER_SEASON);
            }
        }

        Map<String, LocalDate> growthStockCodes = new HashMap<>(stocks);
        findMissingFinanceDataStock(growthStockCodes, financeMapper.findLatestGrowthData());
        for(Map.Entry<String, LocalDate> stock : growthStockCodes.entrySet()) {
            LocalDate queryFrom = stock.getValue();
            while (queryFrom.isBefore(LocalDate.now())) {
                QueryGrowthDataResponse response = baoStockApi.queryGrowthData(new QueryFinanceDataRequest(accessToken, stock.getKey(), queryFrom, QueryFinanceDataRequest.FinanceType.GROWTH));
                if(!handleRemoteResult(QueryFinanceDataRequest.FinanceType.GROWTH.getMethod(), response)) {
                    continue;
                }
                for(QueryGrowthDataResponse.Growth growthToAdd : response.getData()) {
                    financeMapper.insertGrowthData(FinanceAssembler.convertGrowth(growthToAdd));
                }
                queryFrom = queryFrom.plusMonths(MONTHS_PER_SEASON);
            }
        }

        Map<String, LocalDate> balanceStockCodes = new HashMap<>(stocks);
        findMissingFinanceDataStock(balanceStockCodes, financeMapper.findLatestBalanceData());
        for(Map.Entry<String, LocalDate> stock : balanceStockCodes.entrySet()) {
            LocalDate queryFrom = stock.getValue();
            while (queryFrom.isBefore(LocalDate.now())) {
                QueryBalanceDataResponse response = baoStockApi.queryBalanceData(new QueryFinanceDataRequest(accessToken, stock.getKey(), queryFrom, QueryFinanceDataRequest.FinanceType.BALANCE));
                if(!handleRemoteResult(QueryFinanceDataRequest.FinanceType.BALANCE.getMethod(), response)) {
                    continue;
                }
                for(QueryBalanceDataResponse.Balance balanceToAdd : response.getData()) {
                    financeMapper.insertBalanceData(FinanceAssembler.convertBalance(balanceToAdd));
                }
                queryFrom = queryFrom.plusMonths(MONTHS_PER_SEASON);
            }
        }

        Map<String, LocalDate> cashFlowStockCodes = new HashMap<>(stocks);
        findMissingFinanceDataStock(cashFlowStockCodes, financeMapper.findLatestCashFlowData());
        for(Map.Entry<String, LocalDate> stock : cashFlowStockCodes.entrySet()) {
            LocalDate queryFrom = stock.getValue();
            while (queryFrom.isBefore(LocalDate.now())) {
                QueryCashFlowDataResponse response = baoStockApi.queryCashFlowData(new QueryFinanceDataRequest(accessToken, stock.getKey(), queryFrom, QueryFinanceDataRequest.FinanceType.CASH_FLOW));
                if(!handleRemoteResult(QueryFinanceDataRequest.FinanceType.CASH_FLOW.getMethod(), response)) {
                    continue;
                }
                for(QueryCashFlowDataResponse.CashFlow cashFlowToAdd : response.getData()) {
                    financeMapper.insertCashFlowData(FinanceAssembler.convertCashFlow(cashFlowToAdd));
                }
                queryFrom = queryFrom.plusMonths(MONTHS_PER_SEASON);
            }
        }

        Map<String, LocalDate> dupontStockCodes = new HashMap<>(stocks);
        findMissingFinanceDataStock(dupontStockCodes, financeMapper.findLatestDupontData());
        for(Map.Entry<String, LocalDate> stock : dupontStockCodes.entrySet()) {
            LocalDate queryFrom = stock.getValue();
            while (queryFrom.isBefore(LocalDate.now())) {
                QueryDupontDataResponse response = baoStockApi.queryDupontData(new QueryFinanceDataRequest(accessToken, stock.getKey(), queryFrom, QueryFinanceDataRequest.FinanceType.DUPONT));
                if(!handleRemoteResult(QueryFinanceDataRequest.FinanceType.DUPONT.getMethod(), response)) {
                    continue;
                }
                for(QueryDupontDataResponse.Dupont dupontToAdd : response.getData()) {
                    financeMapper.insertDupontData(FinanceAssembler.convertDupont(dupontToAdd));
                }
                queryFrom = queryFrom.plusMonths(MONTHS_PER_SEASON);
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
            log.info("{} last update date []", indexType.getMethod(), queryFrom);
            return;
        }

        while(queryFrom.isBefore(LocalDate.now())) {
            QueryStockIndexResponse recent = baoStockApi.queryIndexStock(new QueryStockIndexRequest(accessToken, indexType, queryFrom));
            if(!handleRemoteResult(indexType.getMethod(), recent)) {
                return;
            }
            Map<String, StockIndexDataObject> old = indexes.stream()
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
                } else {
                    handled.add(exist);
                    StockIndexDataObject changed = new StockIndexDataObject();
                    changed.setId(exist.getId());
                    changed.setUpdateDate(exist.getUpdateDate());
                    indexMapper.update(changed);
                }
            }
            if(old.size() == handled.size()) {
                return;
            }
            old.values().removeAll(handled);
            for(StockIndexDataObject stockIndexDataObject : old.values()) {
                StockIndexDataObject exclude = new StockIndexDataObject();
                exclude.setId(stockIndexDataObject.getId());
                exclude.setExclusionDate(LocalDate.now()); // XXX 存在时间误差
                exclude.setUpdateDate(LocalDate.now());
                indexMapper.update(exclude);
            }
            queryFrom = queryFrom.plusMonths(indexType.getFrequency());
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
