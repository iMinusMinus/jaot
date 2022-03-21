CREATE TABLE BAO_STOCK_BASIC(code VARCHAR(9) NOT NULL PRIMARY KEY, code_name VARCHAR(64), ipo_date DATE NOT NULL, out_date DATE, type INTEGER, status INTEGER);
CREATE TABLE BAO_STOCK_INDUSTRY(code VARCHAR(9) NOT NULL PRIMARY KEY, code_name VARCHAR(64), industry VARCHAR(32), industry_classification VARCHAR(32), update_date DATE NOT NULL);
CREATE TABLE BAO_STOCK_INDEX(id BIGINT PRIMARY KEY AUTO_INCREMENT, type INTEGER NOT NULL, code VARCHAR(9) NOT NULL , code_name VARCHAR(64), inclusion_date DATE NOT NULL, exclusion_date DATE, update_date DATE NOT NULL);
CREATE TABLE BAO_STOCK_PROFIT_DATA(id BIGINT PRIMARY KEY AUTO_INCREMENT, code VARCHAR(9) NOT NULL , pub_date DATE NOT NULL, stat_date DATE NOT NULL, roe_avg NUMBER, np_margin NUMBER, gp_margin NUMBER, net_profit NUMBER, eps_ttm NUMBER, mb_revenue NUMBER, total_share NUMBER, liqa_share NUMBER);
CREATE TABLE BAO_STOCK_OPERATION_DATA(id BIGINT PRIMARY KEY AUTO_INCREMENT, code VARCHAR(9) NOT NULL , pub_date DATE NOT NULL, stat_date DATE NOT NULL, nr_turn_ratio NUMBER, nr_turn_days NUMBER, inv_turn_ratio NUMBER, inv_turn_days NUMBER, ca_turn_ratio NUMBER, asset_turn_ratio NUMBER);
CREATE TABLE BAO_STOCK_GROWTH_DATA(id BIGINT PRIMARY KEY AUTO_INCREMENT, code VARCHAR(9) NOT NULL , pub_date DATE NOT NULL, stat_date DATE NOT NULL, yoy_equity NUMBER, yoy_asset NUMBER, yoy_ni NUMBER, yoy_eps_basic NUMBER, yoy_pni NUMBER);
CREATE TABLE BAO_STOCK_BALANCE_DATA(id BIGINT PRIMARY KEY AUTO_INCREMENT, code VARCHAR(9) NOT NULL , pub_date DATE NOT NULL, stat_date DATE NOT NULL, current_ratio NUMBER, quick_ratio NUMBER, cash_ratio NUMBER, yoy_liability NUMBER, liability_to_asset NUMBER, asset_to_equity NUMBER);
CREATE TABLE BAO_STOCK_CASH_FLOW_DATA(id BIGINT PRIMARY KEY AUTO_INCREMENT, code VARCHAR(9) NOT NULL , pub_date DATE NOT NULL, stat_date DATE NOT NULL, ca_to_asset NUMBER, nca_to_asset NUMBER, tangible_asset_to_asset NUMBER, ebit_to_interest NUMBER, cfo_to_or NUMBER, cfo_to_np NUMBER, cfo_to_gr NUMBER);
CREATE TABLE BAO_STOCK_DUPONT_DATA(id BIGINT PRIMARY KEY AUTO_INCREMENT, code VARCHAR(9) NOT NULL , pub_date DATE NOT NULL, stat_date DATE NOT NULL, dupont_roe NUMBER, dupont_asset_to_equity NUMBER, dupont_asset_turn NUMBER, dupont_pni_to_ni NUMBER, dupont_ni_to_gr NUMBER, dupont_tax_burden NUMBER, dupont_int_burden NUMBER, dupont_ebit_to_gr NUMBER);