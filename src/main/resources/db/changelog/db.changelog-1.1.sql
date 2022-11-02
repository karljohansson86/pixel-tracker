--liquibase formatted sql

--changeset karl:1
create table visits (
    id BIGSERIAL primary key,
    user_id UUID NOT NULL,
    url TEXT NOT NULL,
    visited_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_user
          FOREIGN KEY(user_id)
    	  REFERENCES users(user_id)
);