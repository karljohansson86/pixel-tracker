--liquibase formatted sql

--changeset karl:1
create table users (
    id BIGSERIAL primary key,
    user_id UUID UNIQUE NOT NULL
);

