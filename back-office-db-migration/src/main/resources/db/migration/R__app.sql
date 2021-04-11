CREATE TABLE IF NOT EXISTS `app`
(
    id            VARCHAR(36)  NOT NULL,
    name          VARCHAR(200) NOT NULL,
    signing_key   VARCHAR(100) NOT NULL,
    created_time  DATETIME     NOT NULL,
    created_by    VARCHAR(255) NOT NULL,
    updated_time  DATETIME     NULL,
    updated_by    VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY `idx_app_name` (`name`) USING BTREE
) ENGINE = InnoDB;

SELECT COUNT(*)
INTO @check
FROM `app`
WHERE name = 'Back Office Site';
SET @SQL = IF(@check = 0, 'INSERT INTO `app` (id, name, signing_key, created_time, created_by) VALUES (\'2db99191-2143-4b20-9833-a79c1dc0dbfb\', \'Back Office Site\', \'hxC8gCHoGHUH5ajzugTPyGfw5gHutxEJtLnyZbdti4ctfejPpB\', NOW(), \'DB_INIT_SCRIPT\');', 'select \'Existing Row\';');
PREPARE statement FROM @SQL;
EXECUTE statement;