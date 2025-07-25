package ml.iamwhatiam.baostock.infrastructure.rpc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Constants {
    
    // 版本信息
    public static final String BAOSTOCK_CLIENT_VERSION = "00.8.90";
    public static final String BAOSTOCK_AUTHOR = "baostock.com";
    public static final String BAOSTOCK_SERVER_IP = "www.baostock.com";  // localhost  www.baostock.com  10.25.7.4
    public static final int BAOSTOCK_SERVER_PORT = 10030;

    public static final String BAOSTOCK_SERVER_REAL_TIME_IP = "www.baostock.com";  // 实时行情服务地址 localhost  www.baostock.com
    public static final int BAOSTOCK_SERVER_REAL_TIME_PORT = 10032;  // 实时行情端口

    public static final int BAOSTOCK_PER_PAGE_COUNT = 10000;  // 默认每页查询条数

    public static final int STOCK_CODE_LENGTH = 9;  // 证券代码的长度
    public static final String MESSAGE_SPLIT = "\1";  // 消息内部的分隔符

    public static final String DELIMITER = "\n";  // 分隔符号,消息与消息之间的分隔符

    // 参数各属性间的分隔符，如queryHistoryKData中的fields
    public static final String ATTRIBUTE_SPLIT = ",";
    // 消息头中，消息体长度，占的位数
    public static final int MESSAGE_HEADER_BODYLENGTH = 10;
    // message头长度
    public static final int MESSAGE_HEADER_LENGTH = 21;

    public static final int BAOSTOCK_REALTIME_LIMIT_COUNT = 500;  // 实时行情中，证券代码限制个数

    // 以下为消息请求类型

    public static final String MESSAGE_TYPE_LOGIN_REQUEST = "00";  // 登陆请求
    public static final String MESSAGE_TYPE_LOGIN_RESPONSE = "01";  // 登陆响应
    public static final String MESSAGE_TYPE_LOGOUT_REQUEST = "02";  // 登出请求
    public static final String MESSAGE_TYPE_LOGOUT_RESPONSE = "03";  // 登出响应
    public static final String MESSAGE_TYPE_EXCEPTIONS = "04";  // 错误信息
    public static final String MESSAGE_TYPE_GETKDATA_REQUEST = "11";  // 获取历史K线数据请求
    public static final String MESSAGE_TYPE_GETKDATA_RESPONSE = "12";  // 获取历史K线数据响应
    public static final String MESSAGE_TYPE_QUERYDIVIDENDDATA_REQUEST = "13";  // 估值指标（季频）,股息分红 请求
    public static final String MESSAGE_TYPE_QUERYDIVIDENDDATA_RESPONSE = "14";  // 估值指标（季频）,股息分红 响应
    public static final String MESSAGE_TYPE_ADJUSTFACTOR_REQUEST = "15";  // 复权因子数据 请求
    public static final String MESSAGE_TYPE_ADJUSTFACTOR_RESPONSE = "16";  // 复权因子数据 响应
    public static final String MESSAGE_TYPE_PROFITDATA_REQUEST = "17";  // 季频估值指标盈利能力 请求
    public static final String MESSAGE_TYPE_PROFITDATA_RESPONSE = "18";  // 季频估值指标盈利能力 响应
    public static final String MESSAGE_TYPE_OPERATIONDATA_REQUEST = "19";  // 获取季频估值指标营运能力 请求
    public static final String MESSAGE_TYPE_OPERATIONDATA_RESPONSE = "20";  // 获取季频估值指标营运能力 响应
    public static final String MESSAGE_TYPE_QUERYGROWTHDATA_REQUEST = "21";  // 季频估值指标成长能力 请求
    public static final String MESSAGE_TYPE_QUERYGROWTHDATA_RESPONSE = "22";  // 季频估值指标成长能力 响应
    public static final String MESSAGE_TYPE_QUERYDUPONTDATA_REQUEST = "23";  // 获取季频估值指标杜邦指数  请求
    public static final String MESSAGE_TYPE_QUERYDUPONTDATA_RESPONSE = "24";  // 获取季频估值指标杜邦指数  响应
    public static final String MESSAGE_TYPE_QUERYBALANCEDATA_REQUEST = "25";  // 获取季频估值指标偿债能力  请求
    public static final String MESSAGE_TYPE_QUERYBALANCEDATA_RESPONSE = "26";  // 获取季频估值指标偿债能力  响应
    public static final String MESSAGE_TYPE_QUERYCASHFLOWDATA_REQUEST = "27";  // 获取季频估值指标现金流量   请求
    public static final String MESSAGE_TYPE_QUERYCASHFLOWDATA_RESPONSE = "28";  // 获取季频估值指标现金流量   响应
    public static final String MESSAGE_TYPE_QUERYPERFORMANCEEXPRESSREPORT_REQUEST = "29";  // 公司公告,公司业绩报告 请求
    public static final String MESSAGE_TYPE_QUERYPERFORMANCEEXPRESSREPORT_RESPONSE = "30";  // 公司公告,公司业绩报告 响应
    public static final String MESSAGE_TYPE_QUERYFORECASTREPORT_REQUEST = "31";  // 公司公告,公司业绩预告 请求
    public static final String MESSAGE_TYPE_QUERYFORECASTREPORT_RESPONSE = "32";  // 公司公告,公司业绩预告 响应
    public static final String MESSAGE_TYPE_QUERYTRADEDATES_REQUEST = "33";  // 查询出给定范围的交易日信息 请求
    public static final String MESSAGE_TYPE_QUERYTRADEDATES_RESPONSE = "34";  // 查询出给定范围的交易日信息 响应
    public static final String MESSAGE_TYPE_QUERYALLSTOCK_REQUEST = "35";  // 询给定日期的所有证券信息 请求
    public static final String MESSAGE_TYPE_QUERYALLSTOCK_RESPONSE = "36";  // 询给定日期的所有证券信息 响应

    public static final String MESSAGE_TYPE_LOGIN_REAL_TIME_REQUEST = "37";  // 实时行情登陆请求
    public static final String MESSAGE_TYPE_LOGIN_REAL_TIME_RESPONSE = "38";  // 实时行情登陆响应
    public static final String MESSAGE_TYPE_LOGOUT_REAL_TIME_REQUEST = "39";  // 实时行情登出请求
    public static final String MESSAGE_TYPE_LOGOUT_REAL_TIME_RESPONSE = "40";  // 实时行情登出响应
    public static final String MESSAGE_TYPE_SUBSCRIPTIONS_BY_SECURITYID_REQUEST = "41";  // 订阅行情消息请求
    public static final String MESSAGE_TYPE_SUBSCRIPTIONS_BY_SECURITYID_RESPONSE = "42";  // 订阅行情消息响应
    public static final String MESSAGE_TYPE_CANCEL_SUBSCRIBE_REQUEST = "43";  // 取消订阅行情消息请求
    public static final String MESSAGE_TYPE_CANCEL_SUBSCRIBE_RESPONSE = "44";  // 取消订阅行情消息响应

    public static final String MESSAGE_TYPE_QUERYSTOCKBASIC_REQUEST = "45";  // 获取证券基本资料请求
    public static final String MESSAGE_TYPE_QUERYSTOCKBASIC_RESPONSE = "46";  // 获取证券基本资料响应
    public static final String MESSAGE_TYPE_QUERYDEPOSITRATEDATA_REQUEST = "47";  // 获取存款利率请求
    public static final String MESSAGE_TYPE_QUERYDEPOSITRATEDATA_RESPONSE = "48";  // 获取存款利率响应
    public static final String MESSAGE_TYPE_QUERYLOANRATEDATA_REQUEST = "49";  // 获取贷款利率请求
    public static final String MESSAGE_TYPE_QUERYLOANRATEDATA_RESPONSE = "50";  // 获取贷款利率响应
    public static final String MESSAGE_TYPE_QUREYREQUIREDRESERVERATIODATA_REQUEST = "51";  // 获取存款准备金率请求
    public static final String MESSAGE_TYPE_QUREYREQUIREDRESERVERATIODATA_RESPONSE = "52";  // 获取存款准备金率响应
    public static final String MESSAGE_TYPE_QUERYMONEYSUPPLYDATAMONTH_REQUEST = "53";  // 获取货币供应量请求
    public static final String MESSAGE_TYPE_QUERYMONEYSUPPLYDATAMONTH_RESPONSE = "54";  // 获取货币供应量响应
    public static final String MESSAGE_TYPE_QUERYMONEYSUPPLYDATAYEAR_REQUEST = "55";  // 获取货币供应量（年底余额）请求
    public static final String MESSAGE_TYPE_QUERYMONEYSUPPLYDATAYEAR_RESPONSE = "56";  // 获取货币供应量（年底余额）响应
    public static final String MESSAGE_TYPE_QUERYSHIBORDATA_REQUEST = "57";  // 获取银行间同业拆放利率请求
    public static final String MESSAGE_TYPE_QUERYSHIBORDATA_RESPONSE = "58";  // 获取银行间同业拆放利率响应
    public static final String MESSAGE_TYPE_QUERYSTOCKINDUSTRY_REQUEST = "59";  // 获取行业类别请求
    public static final String MESSAGE_TYPE_QUERYSTOCKINDUSTRY_RESPONSE = "60";  // 获取行业类别响应
    public static final String MESSAGE_TYPE_QUERYHS300STOCKS_REQUEST = "61";  // 获取沪深300成分股请求
    public static final String MESSAGE_TYPE_QUERYHS300STOCKS_RESPONSE = "62";  // 获取沪深300成分股响应
    public static final String MESSAGE_TYPE_QUERYSZ50STOCKS_REQUEST = "63";  // 获取上证50成分股请求
    public static final String MESSAGE_TYPE_QUERYSZ50STOCKS_RESPONSE = "64";  // 获取上证50成分股响应
    public static final String MESSAGE_TYPE_QUERYZZ500STOCKS_REQUEST = "65";  // 获取中证500成分股请求
    public static final String MESSAGE_TYPE_QUERYZZ500STOCKS_RESPONSE = "66";  // 获取中证500成分股响应
    public static final String MESSAGE_TYPE_QUERYTERMINATEDSTOCKS_REQUEST = "67";  // 获取终止上市股票请求
    public static final String MESSAGE_TYPE_QUERYTERMINATEDSTOCKS_RESPONSE = "68";  // 获取终止上市股票响应
    public static final String MESSAGE_TYPE_QUERYSUSPENDEDSTOCKS_REQUEST = "69";   // 获取暂停上市股票列表请求
    public static final String MESSAGE_TYPE_QUERYSUSPENDEDSTOCKS_RESPONSE = "70";  // 获取暂停上市股票列表响应
    public static final String MESSAGE_TYPE_QUERYSTSTOCKS_REQUEST = "71";  // 获取ST股票列表请求
    public static final String MESSAGE_TYPE_QUERYSTSTOCKS_RESPONSE = "72";  // 获取ST股票列表响应
    public static final String MESSAGE_TYPE_QUERYSTARSTSTOCKS_REQUEST = "73";  // 获取*ST股票列表请求
    public static final String MESSAGE_TYPE_QUERYSTARSTSTOCKS_RESPONSE = "74";  // 获取*ST股票列表响应
    public static final String MESSAGE_TYPE_QUERYCPIDATA_REQUEST = "75";  // 获取居民价格消费指数请求
    public static final String MESSAGE_TYPE_QUERYCPIDATA_RESPONSE = "76";  // 获取居民价格消费指数响应
    public static final String MESSAGE_TYPE_QUERYPPIDATA_REQUEST = "77";    // 获取工业品出厂价格指数请求
    public static final String MESSAGE_TYPE_QUERYPPIDATA_RESPONST = "78";    // 获取工业品出厂价格指数响应
    public static final String MESSAGE_TYPE_QUERYPMIDATA_REQUEST = "79";    // 获取采购经理人指数请求
    public static final String MESSAGE_TYPE_QUERYPMIDATA_RESPONST = "80";    // 获取采购经理人指数响应
    public static final String MESSAGE_TYPE_QUERYSTOCKCONCEPT_REQUEST = "81";    // 获取概念分类请求
    public static final String MESSAGE_TYPE_QUERYSTOCKCONCEPT_RESPONST = "82";    // 获取概念分类响应
    public static final String MESSAGE_TYPE_QUERYSTOCKAREA_REQUEST = "83";    // 获取地域分类请求
    public static final String MESSAGE_TYPE_QUERYSTOCKAREA_RESPONST = "84";    // 获取地域分类响应
    public static final String MESSAGE_TYPE_QUERYAMESTOCK_REQUEST = "85";    // 获取中小板分类请求
    public static final String MESSAGE_TYPE_QUERYAMESTOCK_RESPONST = "86";    // 获取中小板分类响应
    public static final String MESSAGE_TYPE_QUERYGEMSTOCK_REQUEST = "87";    // 获取创业板分类请求
    public static final String MESSAGE_TYPE_QUERYGEMSTOCK_RESPONST = "88";    // 获取创业板分类响应
    public static final String MESSAGE_TYPE_QUERYSHHKSTOCK_REQUEST = "89";    // 获取沪港通请求
    public static final String MESSAGE_TYPE_QUERYSHHKSTOCK_RESPONST = "90";    // 获取沪港通响应
    public static final String MESSAGE_TYPE_QUERYSZHKSTOCK_REQUEST = "91";    // 获取深港通请求
    public static final String MESSAGE_TYPE_QUERYSZHKSTOCK_RESPONST = "92";    // 获取深港通响应
    public static final String MESSAGE_TYPE_QUERYSTOCKINRISK_REQUEST = "93";    // 获取风险警示板分类请求
    public static final String MESSAGE_TYPE_QUERYSTOCKINRISK_RESPONST = "94";    // 获取风险警示板分类响应
    public static final String MESSAGE_TYPE_GETKDATAPLUS_REQUEST = "95";   // 获取历史K线数据Plus的请求，行情压缩
    public static final String MESSAGE_TYPE_GETKDATAPLUS_RESPONSE = "96";  // 获取历史K线数据Plus的响应，行情压缩


    // 以上为消息请求类型

    public static final String DEFAULT_START_DATE = "2015-01-01";  // 默认开始时间

    // 方法名及包名的对应关系
    public static final Map<String, String> MESSAGE_PACKAGE_MAPPING = new HashMap<>();

    static {
        MESSAGE_PACKAGE_MAPPING.put("__query_history_k_data_page", "baostock.security.history");
    }


    // 返回消息体进行压缩的响应代码
    // 目前消息体进行压缩的有：获取历史K线数据响应
    public static final List<String> COMPRESSED_MESSAGE_TYPE_TUPLE = Arrays.asList(MESSAGE_TYPE_GETKDATAPLUS_RESPONSE);

    // 以下是错误代码

    public static final String BSERR_SUCCESS = "0";  // 正确返回值
    public static final String BSERR_NO_LOGIN = "10001001";  // 用户未登陆
    public static final String BSERR_USERNAMEORPASSWORD_ERR = "10001002";  // 用户名或密码错误
    public static final String BSERR_GETUSERINFO_FAIL = "10001003";  // 获取用户信息失败
    public static final String BSERR_CLIENT_VESION_EXPIRE = "10001004";  // 客户端版本号过期
    public static final String BSERR_LOGIN_COUNT_LIMIT = "10001005";  // 账号登陆数达到上限
    public static final String BSERR_ACCESS_INSUFFICIENCE = "10001006";  // 用户权限不足
    public static final String BSERR_NEED_ACTIVATE = "10001007";  // 需要登录激活
    public static final String BSERR_USERNAME_EMPTY = "10001008";  // 用户名为空
    public static final String BSERR_PASSWORD_EMPTY = "10001009";  // 密码为空
    public static final String BSERR_LOGOUT_FAIL = "10001010";  // 用户登出失败
    public static final String BSERR_BLACKLIST_USER = "10001011";  // 黑名单用户
    public static final String BSERR_SOCKET_ERR = "10002001";  // 网络错误
    public static final String BSERR_CONNECT_FAIL = "10002002";  // 网络连接失败
    public static final String BSERR_CONNECT_TIMEOUT = "10002003";  // 网络连接超时
    public static final String BSERR_RECVCONNECTION_CLOSED = "10002004";  // 网络接收时连接断开
    public static final String BSERR_SENDSOCK_FAIL = "10002005";  // 网络发送失败
    public static final String BSERR_SENDSOCK_TIMEOUT = "10002006";  // 网络发送超时
    public static final String BSERR_RECVSOCK_FAIL = "10002007";  // 网络接收错误
    public static final String BSERR_RECVSOCK_TIMEOUT = "10002008";  // 网络接收超时
    public static final String BSERR_PARSE_DATA_ERR = "10004001";  // 解析数据错误
    public static final String BSERR_UNGZIP_DATA_FAIL = "10004002";  // gzip 解压失败
    public static final String BSERR_UNKNOWN_ERR = "10004003";  // 客户端未知错误
    public static final String BSERR_OUTOF_BOUNDS = "10004004";  // 数组越界
    public static final String BSERR_INPARAM_EMPTY = "10004005";  // 传入参数为空
    public static final String BSERR_PARAM_ERR = "10004006";  // 参数错误
    public static final String BSERR_START_DATE_ERR = "10004007";  // 起始日期格式不正确
    public static final String BSERR_END_DATE_ERR = "10004008";  // 截止日期格式不正确
    public static final String BSERR_START_BIGTHAN_END = "10004009";  // 起始日期大于终止日期
    public static final String BSERR_DATE_ERR = "10004010";  // 日期格式不正确
    public static final String BSERR_CODE_INVALIED = "10004011";  // 无效的证券代码
    public static final String BSERR_INDICATOR_INVALIED = "10004012";  // 无效的指标
    public static final String BSERR_BEYOND_DATE_SUPPORT = "10004013";  // 超出日期支持范围
    public static final String BSERR_MIXED_CODES_MARKET = "10004014";  // 不支持的混合证券品种
    public static final String BSERR_NO_SUPPORT_CODES_MARKET = "10004015";  // 不支持的证券代码品种
    public static final String BSERR_ORDER_TO_UPPER_LIMIT = "10004016";  // 交易条数超过上限
    public static final String BSERR_NO_SUPPORT_ORDERINFO = "10004017";  // 不支持的交易信息
    public static final String BSERR_INDICATOR_REPEAT = "10004018";  // 指标重复
    public static final String BSERR_MESSAGE_ERROR = "10004019";  // 消息格式不正确
    public static final String BSERR_MESSAGE_CODE_ERROR = "10004020";  // 错误的消息类型
    public static final String BSERR_SYSTEM_ERROR = "10005001";  // 系统级别错误

    // 以上是错误代码


    public static final String CLIENT_ERROR_PARAM = "参数错误，请检查。";  // 客户端参数错误
    
}
