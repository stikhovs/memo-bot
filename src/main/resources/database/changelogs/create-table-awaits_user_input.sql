--liquibase formatted sql
--changeset stikhovs:create-table-awaits_user_input
CREATE TABLE IF NOT EXISTS awaits_user_input (
	id SERIAL PRIMARY KEY,
	chat_id BIGINT NOT NULL,
	input_type VARCHAR(50),
	next_command VARCHAR(100),
	created_at timestamp NOT NULL DEFAULT now(),
	updated_at timestamp NOT NULL DEFAULT now()
);