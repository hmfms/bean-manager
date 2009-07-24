-- ----------------------------------------------------------------------- 
-- Test_Login 
-- ----------------------------------------------------------------------- 

DROP TABLE Test_Login;

-- ----------------------------------------------------------------------- 
-- Test_Login 
-- ----------------------------------------------------------------------- 

CREATE TABLE Test_Login
(
    id VARCHAR(50),
    username VARCHAR(50),
    logindate TIMESTAMP,
    fullname VARCHAR(100),
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX ID ON Test_Login (id);

