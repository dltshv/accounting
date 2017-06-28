DROP TABLE IF EXISTS user_account;
DROP TABLE IF EXISTS user_balance;
DROP TABLE IF EXISTS payment;

CREATE TABLE user_account (
  id      BIGINT PRIMARY KEY,
  name    VARCHAR(30),
  api_key VARCHAR(64)
);

CREATE TABLE user_balance (
  id      BIGINT PRIMARY KEY,
  amount  DECIMAL(16, 9),
  user_id BIGINT
);

CREATE TABLE payment (
  id            BIGINT PRIMARY KEY,
  account       BIGINT,
  type          VARCHAR(30),
  amount_after  DECIMAL(16, 9),
  amount_before DECIMAL(16, 9)
);