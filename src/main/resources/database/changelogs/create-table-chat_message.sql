--liquibase formatted sql
--changeset stikhovs:create-table-chat_message
CREATE TABLE IF NOT EXISTS chat_message (
	id SERIAL PRIMARY KEY,
	chat_id BIGINT NOT NULL,
	message_id BIGINT,
	sender_type VARCHAR,
	has_buttons BOOLEAN,
	created_at timestamp NOT NULL DEFAULT now(),
	updated_at timestamp NOT NULL DEFAULT now()
);