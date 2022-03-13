package ml.iamwhatiam.baostock.infrastructure.web;

import ml.iamwhatiam.baostock.domain.PotentialStockService;
import ml.iamwhatiam.baostock.infrastructure.rpc.BaoStockApi;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryHistoryKDataPlusRequest;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryHistoryKDataPlusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Controller
public class StockController {

    @Resource
    private PotentialStockService potentialStockService;

    @Resource
    private BaoStockApi baoStockApi;

    @Value("${userId}")
    private String userId;

    @GetMapping("/stock/topN")
    @ResponseBody
    public List<StockVO> topN(StockVO condition) {
        int n = 10;
        if(condition != null && condition.getPosition() != null) {
            n = condition.getPosition();
        }
        List<StockVO> result = StockAssembler.convert(potentialStockService.top(n, 3, 0));
        Collections.sort(result);
        return result;
    }

    @GetMapping("/stock/k/{code}")
    @ResponseBody
    public Object k(@PathVariable("code") String code,
                    @RequestParam(value = "startDate", defaultValue = "1990-12-19", required = false) String startDate,
                    @RequestParam(value = "endDate", required = false) String endDate,
                    @RequestParam(value = "frequency", defaultValue = "d", required = false) String frequency,
                    @RequestParam(value = "adjustFlag", defaultValue = "2", required = false) Integer adjustFlag) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
        QueryHistoryKDataPlusResponse.Frequency rate = QueryHistoryKDataPlusResponse.Frequency.getInstance(frequency);
        LocalDate end = null;
        if(endDate != null) {
            end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            end = LocalDate.now();
        }
        return baoStockApi.queryHistoryKDataPlus(new QueryHistoryKDataPlusRequest(userId, code, start, end, rate, adjustFlag));
    }

}
