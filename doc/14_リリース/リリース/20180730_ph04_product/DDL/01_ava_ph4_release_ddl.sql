use ava;

-- ------------------------------------------------------------------------------------------------------------
-- カラム追加
-- ------------------------------------------------------------------------------------------------------------

-- 音声解析ログテーブルカラム追加
ALTER TABLE tbl_t_speech_log 
ADD COLUMN DELETE_DATE datetime(3) AFTER UPDATE_USER_NAME;

-- 企業管理テーブルカラム追加
ALTER TABLE tbl_t_company_management 
ADD COLUMN LOGICAL_DELETE_LOG_KEEP_DAYS integer AFTER VOICE_KEEP_DAYS;
