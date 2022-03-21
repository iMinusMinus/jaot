package ml.iamwhatiam.baostock.infrastructure.rpc;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public abstract class BaoStockRequest implements Serializable {

    private static final AtomicLong INVOKE_ID = new AtomicLong(System.currentTimeMillis());

    protected String userId;

    protected static final String EMPTY = "";

    protected int getCurPageNum() {
        return 1;
    }

    protected int getPerPageCount() {
        return Constants.BAOSTOCK_PER_PAGE_COUNT;
    }

    public BaoStockRequest(String userId) {
        this.userId = userId;
    }

    /**
     * 给netty关联read/write
     *
     * @return 线程安全的递增id
     */
    public Long getId() {
        return INVOKE_ID.getAndIncrement();
    }

    public Long currentId() {
        return INVOKE_ID.get();
    }

    abstract String getRequestCode();

    abstract String encode();

}
