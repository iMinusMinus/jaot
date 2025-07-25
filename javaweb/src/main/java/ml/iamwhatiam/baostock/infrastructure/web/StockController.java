package ml.iamwhatiam.baostock.infrastructure.web;

import ml.iamwhatiam.baostock.domain.PotentialStockService;
import ml.iamwhatiam.baostock.domain.StockEntity;
import ml.iamwhatiam.baostock.infrastructure.BaoStockProperties;
import ml.iamwhatiam.baostock.infrastructure.rpc.BaoStockApi;
import ml.iamwhatiam.baostock.infrastructure.rpc.LoginRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.LoginResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryHistoryKDataPlusRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryHistoryKDataPlusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

@Controller
@RequestMapping("/stock")
public class StockController {

    @Resource
    private PotentialStockService potentialStockService;

    @Resource
    private BaoStockApi baoStockApi;

    @Resource
    private BaoStockProperties baoStockProperties;

    @Value("${baostock.userId}")
    private String accessToken;

    private final AtomicBoolean loggedIn = new AtomicBoolean(false);

    private final AtomicLong lastLoginTime = new AtomicLong(0L);

    @GetMapping("/topN")
    @ResponseBody
    public List<StockVO> topN(PreconditionVO condition) {
        int n = condition.getN();
        Predicate<StockEntity> filter = PotentialStockService.TRADEABLE;
        if (condition.getInitialFilter() == 0) {
            filter = filter.and(PotentialStockService.IPO_FILTER.negate());
        }
        if (condition.getIndexFilter() == 1) {
            filter = filter.and(PotentialStockService.SZ50_FILTER);
        } else if (condition.getIndexFilter() == 7) {
            filter = filter.and(PotentialStockService.INDEX_FILTER);
        }
        // TODO 混合pe、pb与当前股价/历史股价来进行排序
        Comparator<StockEntity> comparator = PotentialStockService.DOWN_POTENTIAL
                .thenComparing(PotentialStockService.UP_POTENTIAL)
                .thenComparing(PotentialStockService.PE)
                .thenComparing(PotentialStockService.PB)
                .thenComparing(StockEntity::getCode);
        List<StockVO> result = StockAssembler.convert(potentialStockService.top(n, filter, comparator));
        Collections.sort(result);
        return result;
    }

    @GetMapping("/login")
    @ResponseBody
    public String login() {
        // session timeout?
        if(System.currentTimeMillis() - lastLoginTime.get() >= baoStockProperties.getSessionTimeout()) {
            loggedIn.set(false);
        }
        // login?
        if(loggedIn.get()) {
            return accessToken;
        }
        // login
        LoginResponse response = baoStockApi.login(new LoginRequest(baoStockProperties.getUserId(), baoStockProperties.getPassword()));
        if(response != null && response.isSuccess()) {
            accessToken = response.getUserId();
            lastLoginTime.set(System.currentTimeMillis());
            loggedIn.set(true);
            return accessToken;
        }
        return null;
    }

    @GetMapping("/k/{code}")
    @ResponseBody
    public Object k(@PathVariable("code") String code,
                    @RequestParam(value = "startDate", defaultValue = "1990-12-19", required = false) String startDate,
                    @RequestParam(value = "endDate", required = false) String endDate,
                    @RequestParam(value = "frequency", defaultValue = "d", required = false) String frequency,
                    @RequestParam(value = "adjustFlag", defaultValue = "2", required = false) Integer adjustFlag) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
        QueryHistoryKDataPlusResponse.Frequency rate = QueryHistoryKDataPlusResponse.Frequency.getInstance(frequency);
        LocalDate end;
        if(endDate != null) {
            end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            end = LocalDate.now();
        }
        return baoStockApi.queryHistoryKDataPlus(new QueryHistoryKDataPlusRequest(accessToken, code, start, end, rate, adjustFlag));
    }

}
