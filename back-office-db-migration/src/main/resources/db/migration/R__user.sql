CREATE TABLE IF NOT EXISTS user
(
    id            INT AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    mobile_phone  VARCHAR(20)  NULL,
    email         VARCHAR(255) NULL,
    password      VARCHAR(100) NOT NULL,
    password_salt VARCHAR(50)  NOT NULL,
    status        VARCHAR(10)  NOT NULL,
    created_time  DATETIME     NOT NULL,
    updated_time  DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY `idx_user_name` (`name`) USING BTREE,
    KEY `idx_user_name_status` (`name`, `status`) USING BTREE
) ENGINE = InnoDB;
