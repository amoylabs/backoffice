CREATE TABLE IF NOT EXISTS role_realm
(
    id            VARCHAR(32)  NOT NULL,
    role_id       VARCHAR(32)  NOT NULL,
    realm_id      VARCHAR(32)  NOT NULL,
    created_time  DATETIME     NOT NULL,
    created_by    VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY `idx_role_id_realm_id` (`role_id`, `realm_id`) USING BTREE
) ENGINE = InnoDB;
