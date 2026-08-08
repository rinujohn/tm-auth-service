CREATE TABLE users (
    user_id    BIGSERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    pwd        VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    create_dt  DATE         NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT uk_users_username UNIQUE (username)
);

CREATE TABLE authorities (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(50) NOT NULL,
    user_id  BIGINT      NOT NULL,
    CONSTRAINT fk_authorities_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_authorities_user_id ON authorities (user_id);
