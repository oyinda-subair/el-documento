CREATE TABLE role_by_id (
    role_id SERIAL PRIMARY KEY,
    title varchar(100) not null UNIQUE,
    role_type varchar(150) null,
    timestamp_created TIMESTAMP not null,
    timestamp_updated timestamp
);