package ml.iamwhatiam.baostock.infrastructure.job;

import lombok.extern.slf4j.Slf4j;
import ml.iamwhatiam.baostock.infrastructure.dao.IndustryDataObject;
import ml.iamwhatiam.baostock.infrastructure.dao.IndustryMapper;
import ml.iamwhatiam.baostock.infrastructure.dao.StockDataObject;
import ml.iamwhatiam.baostock.infrastructure.rpc.BaoStockApi;
import ml.iamwhatiam.baostock.infrastructure.rpc.LoginRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.LoginResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryAllStockRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryAllStockResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryStockBasicRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryStockBasicResponse;
import ml.iamwhatiam.baostock.infrastructure.dao.StockMapper;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryStockIndustryRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryStockIndustryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StockDataJob {

    @Resource
    private BaoStockApi baoStockApi;

    @Value("${userId}")
    private String userId;

    @Value("${password}")
    private String password;

    @Value("${basicData.updateInterval.day}")
    private int updateIntervalDays;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private IndustryMapper industryMapper;

    private LocalDate lastSyncDate;

    @Scheduled(cron = "0 1 23 * * 1,2,3,4,5", zone = "Asia/Shanghai")
    public void updateStocks() {
        log.info("update stock basic info job start");
        // 目前登录userId和后续访问鉴权相同，登录接口没有必要？
        LoginResponse loginResult = login();
        if(loginResult == null || !loginResult.isSuccess()) {
            log.error("login fail: {}", loginResult == null ? "timeout" : loginResult.getErrorMsg());
            return;
        }
        // 当天股市开市情况
        QueryAllStockResponse stocks = queryAllStockData();
        if(stocks == null || !stocks.isSuccess()) {
            log.error("query all stock fail: {}", stocks == null ? "timeout" : stocks.getErrorMsg());
            return;
        }
        log.debug("{}", stocks);
        if(lastSyncDate == null || lastSyncDate.plusDays(updateIntervalDays).compareTo(LocalDate.now()) < 0) {
            // 各股票上市、退市等信息
            mergeStockBasic(stocks);
            // 各股票行业信息
            mergeStockIndustryRelation();
        }
        // TODO
        lastSyncDate = LocalDate.now();
        log.info("update stock basic info job end");
    }

    private void mergeStockIndustryRelation() {
        QueryStockIndustryResponse recent = baoStockApi.queryStockIndustry(new QueryStockIndustryRequest(userId));
        if(recent == null || !recent.isSuccess()) {
            log.error("query stock industry fail: {}", recent == null ? "timeout" : recent.getErrorMsg());
            return;
        }
        List<IndustryDataObject> industries = industryMapper.findAll();
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

    private void mergeStockBasic(QueryAllStockResponse stocks) {
        List<QueryStockBasicResponse.Stock> recent = new ArrayList<>();
        for(QueryAllStockResponse.Stock stock : stocks.getData()) {
            QueryStockBasicResponse stockBasic = queryStockBasic(stock.getCode(), stock.getCodeName());
            if(stockBasic == null || !stockBasic.isSuccess()) {
                log.error("query stock basic fail: {}", stockBasic == null ? "timeout" : stockBasic.getErrorMsg());
                continue;
            }
            List<QueryStockBasicResponse.Stock> stockBasicResult = stockBasic.getData();
            recent.addAll(stockBasicResult);
        }
        List<StockDataObject> stockDataObjects = stockMapper.findAll();
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

    /**
     * 获取指定交易日期所有股票列表。通过API接口获取证券代码及股票交易状态信息，与日K线数据同时更新。可以通过参数‘某交易日’获取数据（包括：A股、指数）
     *
     * @return all stock
     */
    private QueryAllStockResponse queryAllStockData() {
        QueryAllStockRequest request = new QueryAllStockRequest(userId);
        return baoStockApi.queryAllStock(request);
    }

    /**
     * 通过API接口获取证券基本资料，可以通过参数设置获取对应证券代码、证券名称的数据
     *
     * @param code A股股票代码，sh或sz.+6位数字代码，或者指数代码，如：sh.601398。sh：上海；sz：深圳。可以为空
     * @param codeName 股票名称，支持模糊查询，可以为空
     * @return stock basic info
     */
    private QueryStockBasicResponse queryStockBasic(String code, String codeName) {
        QueryStockBasicRequest request = new QueryStockBasicRequest(userId, code);
        return baoStockApi.queryStockBasic(request);
    }

    /**
     * 登录系统
     *
     * @return login result
     */
    private LoginResponse login() {
        LoginRequest request = new LoginRequest(userId, password);
        return baoStockApi.login(request);
    }

}
