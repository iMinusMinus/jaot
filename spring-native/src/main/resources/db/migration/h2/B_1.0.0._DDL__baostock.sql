CREATE TABLE BAO_STOCK_BASIC(code VARCHAR(9) NOT NULL PRIMARY KEY, code_name VARCHAR(64), ipo_date DATE NOT NULL, out_date DATE, type INTEGER, status INTEGER);
CREATE TABLE BAO_STOCK_INDUSTRY(code VARCHAR(9) NOT NULL PRIMARY KEY, code_name VARCHAR(64), industry VARCHAR(32), industry_classification VARCHAR(32), update_date DATE NOT NULL);