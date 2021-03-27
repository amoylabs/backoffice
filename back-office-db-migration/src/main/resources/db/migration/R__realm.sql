CREATE TABLE IF NOT EXISTS realm
(
    id            VARCHAR(36)  NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    description   VARCHAR(200) NOT NULL,
    created_time  DATETIME     NOT NULL,
    created_by    VARCHAR(255) NOT NULL,
    updated_time  DATETIME     NULL,
    updated_by    VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY `idx_realm_name` (`name`) USING BTREE
) ENGINE = InnoDB;
