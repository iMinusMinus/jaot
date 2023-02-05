package ml.iamwhatiam.baostock.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public interface PotentialStockService {

    Predicate<StockEntity> TRADEABLE = (stock) -> stock.getOutDate() == null;

    Predicate<StockEntity> INDEX_FILTER = (stock) -> Arrays.stream(StockIndexType.values()).anyMatch(t->(t.getValue() & stock.getType().intValue()) != 0);

    Predicate<StockEntity> SZ50_FILTER = (stock) -> StockIndexType.getInstance(stock.getType()) == StockIndexType.SZ50;

    Predicate<StockEntity> IPO_FILTER = (stock) -> stock.getIpoDate().plusYears(1).isAfter(LocalDate.now());

    /**
     * 市盈率可能为负。是否新行业。值需规整到100的绝对值以内
     */
    Comparator<StockEntity> PE = (prev, next) -> compare(prev.getValue().getPe(), next.getValue().getPe()).intValue();

    /**
     * 市净率可能为负，是否重资产行业。值需规整到100的绝对值以内
     */
    Comparator<StockEntity> PB = (prev, next) -> compare(prev.getValue().getPb(), next.getValue().getPb()).intValue();

    // [3,5],[5,-3],[3,-5],[-5,-3]
    static BigDecimal compare(BigDecimal prev, BigDecimal next) {
        BigDecimal value = prev.subtract(next);
        BigDecimal direction = prev.multiply(next);
        return direction.compareTo(BigDecimal.ZERO) < 0 && prev.compareTo(BigDecimal.ZERO) < 0 ? value.negate() : value;
    }

    /**
     * 当前股价与最高股价比值，需考虑间隔时间
     */
    Comparator<StockEntity> UP_POTENTIAL = (prev, next) -> prev.getValue().getPrice().divide(prev.getValue().getHighPrice(), 2, RoundingMode.HALF_UP)
            .subtract(next.getValue().getPrice().divide(next.getValue().getHighPrice(), 2, RoundingMode.HALF_UP)).multiply(BigDecimal.TEN).intValue();

    /**
     * 当前股价与最低股价比值，需考虑间隔时间
     */
    Comparator<StockEntity> DOWN_POTENTIAL = (prev, next) -> prev.getValue().getPrice().divide(prev.getValue().getLowPrice(), 2, RoundingMode.HALF_UP)
            .subtract(next.getValue().getPrice().divide(next.getValue().getLowPrice(), 2, RoundingMode.HALF_UP)).multiply(BigDecimal.TEN).intValue();

    List<StockEntity> top(int total, Predicate<StockEntity> filter, Comparator<StockEntity> comparator);
}
