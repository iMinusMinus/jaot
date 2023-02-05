package ml.iamwhatiam.baostock.infrastructure.web;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.iamwhatiam.baostock.domain.StockIndexType;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@Setter
@ToString
public class PreconditionVO implements Serializable {

    /**
     * 股票指数类型：1, 仅上证50; 7, 所有指数; 0, 所有股票
     */
    private int indexFilter = Arrays.stream(StockIndexType.values()).map(t->t.getValue()).reduce((p, n)->p | n).orElse(0);

    /**
     * 股票新旧类型：1, 含次新股; 0, 仅上市一年以上股票
     */
    private int initialFilter;

    /**
     * 需要推荐的股票数
     */
    private int n = 10;

}
