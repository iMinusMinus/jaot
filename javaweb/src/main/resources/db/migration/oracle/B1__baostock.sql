-- Create table
create table BAO_STOCK_BASIC
(
    code      VARCHAR2(9) not null,
    code_name VARCHAR2(64),
    ipo_date  DATE not null,
    out_date  DATE,
    type      NUMBER(1) not null,
    status    NUMBER(1) not null
)
    tablespace STOCK
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table
comment on table BAO_STOCK_BASIC
  is '证券基本资料';
-- Add comments to the columns
comment on column BAO_STOCK_BASIC.code
  is '证券代码';
comment on column BAO_STOCK_BASIC.code_name
  is '证券代码';
comment on column BAO_STOCK_BASIC.ipo_date
  is '上市日期';
comment on column BAO_STOCK_BASIC.out_date
  is '退市日期';
comment on column BAO_STOCK_BASIC.type
  is '证券类型，其中1：股票，2：指数，3：其它，4：可转债，5：ETF';
comment on column BAO_STOCK_BASIC.status
  is '上市状态，其中1：上市，0：退市';
-- Create/Recreate primary, unique and foreign key constraints
alter table BAO_STOCK_BASIC
    add constraint PK_BAO_STOCK_BASIC primary key (CODE)
    using index
  tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;




-- Create table
create table BAO_STOCK_INDUSTRY
(
    code                    VARCHAR2(9) not null,
    code_name               VARCHAR2(64),
    industry                VARCHAR2(32),
    industry_classification VARCHAR2(32),
    update_date             DATE not null
)
    tablespace STOCK
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table
comment on table BAO_STOCK_INDUSTRY
  is '行业分类';
-- Add comments to the columns
comment on column BAO_STOCK_INDUSTRY.code
  is '证券代码';
comment on column BAO_STOCK_INDUSTRY.code_name
  is '证券名称';
comment on column BAO_STOCK_INDUSTRY.industry
  is '所属行业';
comment on column BAO_STOCK_INDUSTRY.industry_classification
  is '所属行业类别';
comment on column BAO_STOCK_INDUSTRY.update_date
  is '更新日期';
-- Create/Recreate indexes
create index IDX_BAO_STOCK_INDUSTRY on BAO_STOCK_INDUSTRY (CODE)
    tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;


-- Create sequence
create sequence SEQ_BAO_STOCK_INDEX
    minvalue 1
    maxvalue 9223372036854775807
    start with 1
    increment by 1
    cache 100;
-- Create table
create table BAO_STOCK_INDEX
(
    id             NUMBER not null,
    type           NUMBER(4) not null,
    code           VARCHAR2(9) not null,
    code_name      VARCHAR2(64),
    inclusion_date DATE not null,
    exclusion_date DATE,
    update_date    DATE not null
)
    tablespace STOCK
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table
comment on table BAO_STOCK_INDEX
  is '指数信息';
-- Add comments to the columns
comment on column BAO_STOCK_INDEX.type
  is '指数类型，1:上证50,2:沪深300,4:中证500';
comment on column BAO_STOCK_INDEX.code
  is '证券代码';
comment on column BAO_STOCK_INDEX.code_name
  is '证券名称';
comment on column BAO_STOCK_INDEX.inclusion_date
  is '纳入该指数日期';
comment on column BAO_STOCK_INDEX.exclusion_date
  is '剔除出该指数日期';
comment on column BAO_STOCK_INDEX.update_date
  is '更新日期';
-- Create/Recreate indexes
create index IDX_BAO_STOCK_INDEX_CODE on BAO_STOCK_INDEX (CODE)
    tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;



-- Create sequence
create sequence SEQ_BAO_STOCK_PROFIT_DATA
    minvalue 1
    maxvalue 9223372036854775807
    start with 1
    increment by 1
    cache 100;

-- Create table
create table BAO_STOCK_PROFIT_DATA
(
    id          NUMBER not null,
    code        VARCHAR2(9) not null,
    pub_date    DATE not null,
    stat_date   DATE not null,
    roe_avg     NUMBER(16,6),
    np_margin   NUMBER(16,6),
    gp_margin   NUMBER(16,6),
    net_profit  NUMBER(24,8),
    eps_ttm     NUMBER(24,6),
    mb_revenue  NUMBER(32,8),
    total_share NUMBER(32,2),
    liqa_share  NUMBER(32,2)
)
    tablespace STOCK
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table
comment on table BAO_STOCK_PROFIT_DATA
  is '季频盈利能力';
-- Add comments to the columns
comment on column BAO_STOCK_PROFIT_DATA.code
  is '证券代码';
comment on column BAO_STOCK_PROFIT_DATA.pub_date
  is '公司发布财报的日期';
comment on column BAO_STOCK_PROFIT_DATA.stat_date
  is '财报统计的季度的最后一天, 比如2017-03-31';
comment on column BAO_STOCK_PROFIT_DATA.roe_avg
  is '净资产收益率(平均)(%)，归属母公司股东净利润/[(期初归属母公司股东的权益+期末归属母公司股东的权益)/2]*100%';
comment on column BAO_STOCK_PROFIT_DATA.np_margin
  is '销售净利率(%)，净利润/营业收入*100%';
comment on column BAO_STOCK_PROFIT_DATA.gp_margin
  is '净利润/营业收入*100%，毛利/营业收入*100%=(营业收入-营业成本)/营业收入*100%';
comment on column BAO_STOCK_PROFIT_DATA.net_profit
  is '净利润(元)';
comment on column BAO_STOCK_PROFIT_DATA.eps_ttm
  is '每股收益，归属母公司股东的净利润TTM/最新总股本';
comment on column BAO_STOCK_PROFIT_DATA.mb_revenue
  is '主营营业收入(元)';
comment on column BAO_STOCK_PROFIT_DATA.total_share
  is '总股本';
comment on column BAO_STOCK_PROFIT_DATA.liqa_share
  is '流通股本';
-- Create/Recreate indexes
create index IDX_BAO_STOCK_PROFIT on BAO_STOCK_PROFIT_DATA (CODE, STAT_DATE)
    tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;
-- Create/Recreate primary, unique and foreign key constraints
alter table BAO_STOCK_PROFIT_DATA
    add constraint PK_BAO_STOCK_PROFIT primary key (ID)
    using index
  tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;



-- Create sequence
create sequence SEQ_BAO_STOCK_OPERATION_DATA
    minvalue 1
    maxvalue 9223372036854775807
    start with 1
    increment by 1
    cache 100;

-- Create table
create table BAO_STOCK_OPERATION_DATA
(
    id               NUMBER not null,
    code             VARCHAR2(9) not null,
    pub_date         DATE not null,
    stat_date        DATE not null,
    nr_turn_ratio    NUMBER(16,6),
    nr_turn_days     NUMBER(16,6),
    inv_turn_ratio   NUMBER(16,6),
    inv_turn_days    NUMBER(16,6),
    ca_turn_ratio    NUMBER(16,6),
    asset_turn_ratio NUMBER(16,6)
)
    tablespace STOCK
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table
comment on table BAO_STOCK_OPERATION_DATA
  is '季频营运能力';
-- Add comments to the columns
comment on column BAO_STOCK_OPERATION_DATA.code
  is '证券代码';
comment on column BAO_STOCK_OPERATION_DATA.pub_date
  is '公司发布财报的日期';
comment on column BAO_STOCK_OPERATION_DATA.stat_date
  is '财报统计的季度的最后一天, 比如2017-03-31';
comment on column BAO_STOCK_OPERATION_DATA.nr_turn_ratio
  is '应收账款周转率(次)，营业收入/[(期初应收票据及应收账款净额+期末应收票据及应收账款净额)/2]';
comment on column BAO_STOCK_OPERATION_DATA.nr_turn_days
  is '应收账款周转天数(天)，季报天数/应收账款周转率(一季报：90天，中报：180天，三季报：270天，年报：360天)';
comment on column BAO_STOCK_OPERATION_DATA.inv_turn_ratio
  is '存货周转率(次)，营业成本/[(期初存货净额+期末存货净额)/2]';
comment on column BAO_STOCK_OPERATION_DATA.inv_turn_days
  is '存货周转天数(天)，季报天数/存货周转率(一季报：90天，中报：180天，三季报：270天，年报：360天)';
comment on column BAO_STOCK_OPERATION_DATA.ca_turn_ratio
  is '流动资产周转率(次)，营业总收入/[(期初流动资产+期末流动资产)/2]';
comment on column BAO_STOCK_OPERATION_DATA.asset_turn_ratio
  is '总资产周转率，营业总收入/[(期初资产总额+期末资产总额)/2]';
-- Create/Recreate indexes
create index IDX_BAO_STOCK_OPERATION on BAO_STOCK_OPERATION_DATA (CODE, STAT_DATE)
    tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;
-- Create/Recreate primary, unique and foreign key constraints
alter table BAO_STOCK_OPERATION_DATA
    add constraint PK_BAO_STOCK_OPERATION primary key (ID)
    using index
  tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;



-- Create sequence
create sequence SEQ_BAO_STOCK_GROWTH_DATA
    minvalue 1
    maxvalue 9223372036854775807
    start with 1
    increment by 1
    cache 100;

-- Create table
create table BAO_STOCK_GROWTH_DATA
(
    id            NUMBER not null,
    code          VARCHAR2(9) not null,
    pub_date      DATE not null,
    stat_date     DATE not null,
    yoy_equity    NUMBER(16,6),
    yoy_asset     NUMBER(16,6),
    yoy_ni        NUMBER(16,6),
    yoy_eps_basic NUMBER(16,6),
    yoy_pni       NUMBER(16,6)
)
    tablespace STOCK
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table
comment on table BAO_STOCK_GROWTH_DATA
  is '季频成长能力';
-- Add comments to the columns
comment on column BAO_STOCK_GROWTH_DATA.code
  is '证券代码';
comment on column BAO_STOCK_GROWTH_DATA.pub_date
  is '公司发布财报的日期';
comment on column BAO_STOCK_GROWTH_DATA.stat_date
  is '财报统计的季度的最后一天, 比如2017-03-31';
comment on column BAO_STOCK_GROWTH_DATA.yoy_equity
  is '净资产同比增长率，(本期净资产-上年同期净资产)/上年同期净资产的绝对值*100%';
comment on column BAO_STOCK_GROWTH_DATA.yoy_asset
  is '总资产同比增长率，(本期总资产-上年同期总资产)/上年同期总资产的绝对值*100%';
comment on column BAO_STOCK_GROWTH_DATA.yoy_ni
  is '净利润同比增长率，(本期净利润-上年同期净利润)/上年同期净利润的绝对值*100%';
comment on column BAO_STOCK_GROWTH_DATA.yoy_eps_basic
  is '基本每股收益同比增长率，(本期基本每股收益-上年同期基本每股收益)/上年同期基本每股收益的绝对值*100%';
comment on column BAO_STOCK_GROWTH_DATA.yoy_pni
  is '归属母公司股东净利润同比增长率，(本期归属母公司股东净利润-上年同期归属母公司股东净利润)/上年同期归属母公司股东净利润的绝对值*100%';
-- Create/Recreate indexes
create index IDX_BAO_STOCK_GROWTH on BAO_STOCK_GROWTH_DATA (CODE, STAT_DATE)
    tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;
-- Create/Recreate primary, unique and foreign key constraints
alter table BAO_STOCK_GROWTH_DATA
    add constraint PK_BAO_STOCK_GROWTH primary key (ID)
    using index
  tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;



-- Create sequence
create sequence SEQ_BAO_STOCK_BALANCE_DATA
    minvalue 1
    maxvalue 9223372036854775807
    start with 1
    increment by 1
    cache 100;
-- Create table
create table BAO_STOCK_BALANCE_DATA
(
    id                 NUMBER not null,
    code               VARCHAR2(9) not null,
    pub_date           DATE not null,
    stat_date          DATE not null,
    current_ratio      NUMBER(16,6),
    quick_ratio        NUMBER(16,6),
    cash_ratio         NUMBER(16,6),
    yoy_liability      NUMBER(16,6),
    liability_to_asset NUMBER(16,6),
    asset_to_equity    NUMBER(16,6)
)
    tablespace STOCK
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table
comment on table BAO_STOCK_BALANCE_DATA
  is '季频偿债能力';
-- Add comments to the columns
comment on column BAO_STOCK_BALANCE_DATA.code
  is '证券代码';
comment on column BAO_STOCK_BALANCE_DATA.pub_date
  is '公司发布财报的日期';
comment on column BAO_STOCK_BALANCE_DATA.stat_date
  is '财报统计的季度的最后一天, 比如2017-03-31';
comment on column BAO_STOCK_BALANCE_DATA.current_ratio
  is '流动比率，流动资产/流动负债';
comment on column BAO_STOCK_BALANCE_DATA.quick_ratio
  is '速动比率，(流动资产-存货净额)/流动负债';
comment on column BAO_STOCK_BALANCE_DATA.cash_ratio
  is '现金比率，(货币资金+交易性金融资产)/流动负债';
comment on column BAO_STOCK_BALANCE_DATA.yoy_liability
  is '总负债同比增长率，(本期总负债-上年同期总负债)/上年同期中负债的绝对值*100%';
comment on column BAO_STOCK_BALANCE_DATA.liability_to_asset
  is '资产负债率，负债总额/资产总额';
comment on column BAO_STOCK_BALANCE_DATA.asset_to_equity
  is '权益乘数，资产总额/股东权益总额=1/(1-资产负债率)';
-- Create/Recreate indexes
create index IDX_BAO_STOCK_BALANCE on BAO_STOCK_BALANCE_DATA (CODE, STAT_DATE)
    tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;
-- Create/Recreate primary, unique and foreign key constraints
alter table BAO_STOCK_BALANCE_DATA
    add constraint PK_BAO_STOCK_BALANCE primary key (ID)
    using index
  tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;



-- Create sequence
create sequence SEQ_BAO_STOCK_CASH_FLOW_DATA
    minvalue 1
    maxvalue 9223372036854775807
    start with 1
    increment by 1
    cache 100;

-- Create table
create table BAO_STOCK_CASH_FLOW_DATA
(
    id                      NUMBER not null,
    code                    VARCHAR2(9) not null,
    pub_date                DATE not null,
    stat_date               DATE not null,
    ca_to_asset             NUMBER(16,6),
    nca_to_asset            NUMBER(16,6),
    tangible_asset_to_asset NUMBER(16,6),
    ebit_to_interest        NUMBER(16,6),
    cfo_to_or               NUMBER(16,6),
    cfo_to_np               NUMBER(16,6),
    cfo_to_gr               NUMBER(16,6)
)
    tablespace STOCK
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table
comment on table BAO_STOCK_CASH_FLOW_DATA
  is '季频现金流量';
-- Add comments to the columns
comment on column BAO_STOCK_CASH_FLOW_DATA.code
  is '证券代码';
comment on column BAO_STOCK_CASH_FLOW_DATA.pub_date
  is '公司发布财报的日期';
comment on column BAO_STOCK_CASH_FLOW_DATA.stat_date
  is '财报统计的季度的最后一天, 比如2017-03-31';
comment on column BAO_STOCK_CASH_FLOW_DATA.ca_to_asset
  is '流动资产除以总资产';
comment on column BAO_STOCK_CASH_FLOW_DATA.nca_to_asset
  is '非流动资产除以总资产';
comment on column BAO_STOCK_CASH_FLOW_DATA.tangible_asset_to_asset
  is '有形资产除以总资产';
comment on column BAO_STOCK_CASH_FLOW_DATA.ebit_to_interest
  is '已获利息倍数，息税前利润/利息费用';
comment on column BAO_STOCK_CASH_FLOW_DATA.cfo_to_or
  is '经营活动产生的现金流量净额除以营业收入';
comment on column BAO_STOCK_CASH_FLOW_DATA.cfo_to_np
  is '经营性现金净流量除以净利润';
comment on column BAO_STOCK_CASH_FLOW_DATA.cfo_to_gr
  is '经营性现金净流量除以营业总收入';
-- Create/Recreate indexes
create index IDX_BAO_STOCK_CASHFLOW on BAO_STOCK_CASH_FLOW_DATA (CODE, STAT_DATE)
    tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;
-- Create/Recreate primary, unique and foreign key constraints
alter table BAO_STOCK_CASH_FLOW_DATA
    add constraint PK_BAO_STOCK_CASHFLOW primary key (ID)
    using index
  tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;



-- Create sequence
create sequence SEQ_BAO_STOCK_DUPONT_DATA
    minvalue 1
    maxvalue 9223372036854775807
    start with 1
    increment by 1
    cache 100;
-- Create table
create table BAO_STOCK_DUPONT_DATA
(
    id                     NUMBER not null,
    code                   VARCHAR2(9) not null,
    pub_date               DATE not null,
    stat_date              DATE not null,
    dupont_roe             NUMBER(16,6),
    dupont_asset_to_equity NUMBER(16,6),
    dupont_asset_turn      NUMBER(16,6),
    dupont_pni_to_ni       NUMBER(16,6),
    dupont_ni_to_gr        NUMBER(16,6),
    dupont_tax_burden      NUMBER(16,6),
    dupont_int_burden      NUMBER(16,6),
    dupont_ebit_to_gr      NUMBER(16,6)
)
    tablespace STOCK
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table
comment on table BAO_STOCK_DUPONT_DATA
  is '季频杜邦指数';
-- Add comments to the columns
comment on column BAO_STOCK_DUPONT_DATA.code
  is '证券代码';
comment on column BAO_STOCK_DUPONT_DATA.pub_date
  is '公司发布财报的日期';
comment on column BAO_STOCK_DUPONT_DATA.stat_date
  is '财报统计的季度的最后一天, 比如2017-03-31';
comment on column BAO_STOCK_DUPONT_DATA.dupont_roe
  is '净资产收益率，归属母公司股东净利润/[(期初归属母公司股东的权益+期末归属母公司股东的权益)/2]*100%';
comment on column BAO_STOCK_DUPONT_DATA.dupont_asset_to_equity
  is '权益乘数，反映企业财务杠杆效应强弱和财务风险。平均总资产/平均归属于母公司的股东权益';
comment on column BAO_STOCK_DUPONT_DATA.dupont_asset_turn
  is '总资产周转率，反映企业资产管理效率的指标。营业总收入/[(期初资产总额+期末资产总额)/2]';
comment on column BAO_STOCK_DUPONT_DATA.dupont_pni_to_ni
  is '归属母公司股东的净利润/净利润，反映母公司控股子公司百分比。如果企业追加投资，扩大持股比例，则本指标会增加';
comment on column BAO_STOCK_DUPONT_DATA.dupont_ni_to_gr
  is '净利润/营业总收入，反映企业销售获利率';
comment on column BAO_STOCK_DUPONT_DATA.dupont_tax_burden
  is '净利润/利润总额，反映企业税负水平，该比值高则税负较低。净利润/利润总额=1-所得税/利润总额';
comment on column BAO_STOCK_DUPONT_DATA.dupont_int_burden
  is '利润总额/息税前利润，反映企业利息负担，该比值高则税负较低。利润总额/息税前利润=1-利息费用/息税前利润';
comment on column BAO_STOCK_DUPONT_DATA.dupont_ebit_to_gr
  is '息税前利润/营业总收入，反映企业经营利润率，是企业经营获得的可供全体投资人（股东和债权人）分配的盈利占企业全部营收收入的百分比';
-- Create/Recreate indexes
create index IDX_BAO_STOCK_DUPONT on BAO_STOCK_DUPONT_DATA (CODE, STAT_DATE)
    tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;
-- Create/Recreate primary, unique and foreign key constraints
alter table BAO_STOCK_DUPONT_DATA
    add constraint PK_BAO_STOCK_DUPONT primary key (ID)
    using index
  tablespace STOCK
  pctfree 10
  initrans 2
  maxtrans 255;




-- Oracle Advanced Queue
/*
GRANT EXECUTE ON DBMS_AQ to Warren;
GRANT EXECUTE ON DBMS_AQADM to Warren;
GRANT AQ_ADMINISTRATOR_ROLE TO Warren;
GRANT ADMINISTER DATABASE TRIGGER TO Warren;
BEGIN
    DBMS_AQADM.CREATE_QUEUE_TABLE(QUEUE_TABLE => 'Warren.AQ_TABLE', QUEUE_PAYLOAD_TYPE => 'RAW', COMPATIBLE  => '10.0');
    DBMS_AQADM.CREATE_QUEUE(QUEUE_NAME => 'Warren.AQ_QUEUE', QUEUE_TABLE => 'Warren.AQ_TABLE');
    DBMS_AQADM.START_QUEUE('Warren.AQ_QUEUE');
    --DBMS_AQADM.STOP_QUEUE('Warren.AQ_QUEUE');
    --DBMS_AQADM.DROP_QUEUE_TABLE(QUEUE_TABLE => 'Warren.AQ_TABLE', FORCE => TRUE);
END;
/
*/