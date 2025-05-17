--liquibase formatted sql
--changeset stikhovs:create-table-feedback
CREATE TABLE IF NOT EXISTS feedback (
	id SERIAL PRIMARY KEY,
	source_chat_id BIGINT NOT NULL,
	admin_chat_id BIGINT NOT NULL,
	message_id BIGINT NOT NULL,
	status VARCHAR,
	created_at timestamp NOT NULL DEFAULT now(),
	updated_at timestamp NOT NULL DEFAULT now()
);