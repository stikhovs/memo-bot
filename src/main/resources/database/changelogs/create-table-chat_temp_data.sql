--liquibase formatted sql
--changeset stikhovs:create-table-chat_temp_data
CREATE TABLE IF NOT EXISTS chat_temp_data (
	id SERIAL PRIMARY KEY,
	chat_id BIGINT NOT NULL,
	data TEXT,
	created_at timestamp NOT NULL DEFAULT now(),
	updated_at timestamp NOT NULL DEFAULT now()
);