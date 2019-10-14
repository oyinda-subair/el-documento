CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE user_by_id (
    user_id uuid DEFAULT uuid_generate_v4 (),
    name varchar(100) not null,
    username varchar(100) not null,
    email varchar(150) not null,
    password varchar(150) not null,
    role_id integer REFERENCES role_by_id(role_id) ON DELETE CASCADE,
    timestamp_created TIMESTAMP not null,
    timestamp_updated timestamp,
    UNIQUE (user_id, username, email),
    PRIMARY KEY (user_id)
);