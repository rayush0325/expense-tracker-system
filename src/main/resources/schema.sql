create database expense_tracker_dev;
use expense_tracker_dev;

create table users(
	user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name varchar(100) NOT NULL,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    role ENUM ('user', 'admin') NOT NULL DEFAULT 'user',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_email UNIQUE (email)
);
desc users;