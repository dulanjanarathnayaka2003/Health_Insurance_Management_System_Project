-- Fix system_config table structure
ALTER TABLE system_config 
MODIFY COLUMN config_key BIGINT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (config_key);

-- If the above fails due to existing data, use this alternative approach:
-- 1. Create a backup of the data
-- CREATE TABLE system_config_backup AS SELECT * FROM system_config;
-- 
-- 2. Drop and recreate the table with correct structure
-- DROP TABLE system_config;
-- CREATE TABLE system_config (
--     config_key BIGINT NOT NULL AUTO_INCREMENT,
--     value VARCHAR(255),
--     config_name VARCHAR(255),
--     PRIMARY KEY (config_key)
-- );
-- 
-- 3. Restore the data (if needed)
-- INSERT INTO system_config (config_key, value, config_name)
-- SELECT config_key, value, config_name FROM system_config_backup;
