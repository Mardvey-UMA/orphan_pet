CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    vk_id BIGINT,
    provider VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL,
    account_locked BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);


CREATE TABLE role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

CREATE TABLE token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    revoked BOOLEAN NOT NULL,
    expired BOOLEAN NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT NOW(),
    user_id BIGINT,
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE mail_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    validated_at TIMESTAMP,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_mail_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
