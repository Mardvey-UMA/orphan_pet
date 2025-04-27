CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255),
    username VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    city VARCHAR(255),
    about_me VARCHAR(255)
);

CREATE TABLE animal (
    id BIGSERIAL PRIMARY KEY,
    body_mass NUMERIC,
    height NUMERIC,
    temperature NUMERIC,
    activity_level INT,
    appetite_level INT,
    birth_date DATE,
    created_at DATE,
    name VARCHAR(255),
    description TEXT
);

CREATE TABLE attribute (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    animal_id BIGINT,
    CONSTRAINT fk_attribute_animal FOREIGN KEY (animal_id) REFERENCES animal(id) ON DELETE CASCADE
);

CREATE TABLE value (
    id BIGSERIAL PRIMARY KEY,
    value VARCHAR(1024),
    attribute_id INT,
    CONSTRAINT fk_value_attribute FOREIGN KEY (attribute_id) REFERENCES attribute(id) ON DELETE CASCADE
);

CREATE TABLE photo (
    id BIGSERIAL PRIMARY KEY,
    object_key VARCHAR(512) UNIQUE,
    created_at DATE
);

CREATE TABLE user_photo (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    photo_id BIGINT,
    CONSTRAINT fk_user_photo_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_photo_photo FOREIGN KEY (photo_id) REFERENCES photo(id) ON DELETE SET NULL
);

CREATE TABLE animal_photo (
    id BIGSERIAL PRIMARY KEY,
    animal_id BIGINT,
    photo_id BIGINT,
    CONSTRAINT fk_animal_photo_animal FOREIGN KEY (animal_id) REFERENCES animal(id) ON DELETE CASCADE,
    CONSTRAINT fk_animal_photo_photo FOREIGN KEY (photo_id) REFERENCES photo(id) ON DELETE SET NULL
);

CREATE TABLE document (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(255),
    object_key VARCHAR(512),
    document_name VARCHAR(255),
    animal_id BIGINT,
    CONSTRAINT fk_document_animal FOREIGN KEY (animal_id) REFERENCES animal(id) ON DELETE CASCADE
);

CREATE TABLE animal_user (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    animal_id BIGINT,
    CONSTRAINT fk_animal_user_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_animal_user_animal FOREIGN KEY (animal_id) REFERENCES animal(id) ON DELETE CASCADE
);

CREATE TABLE animal_status_log (
    id BIGSERIAL PRIMARY KEY,
    log_date DATE,
    notes TEXT,
    updated_at DATE,
    mass_change NUMERIC,
    height_change NUMERIC,
    temperature_change NUMERIC,
    activity_level_change INT,
    appetite_level_change INT,
    animal_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_status_log_animal FOREIGN KEY (animal_id) REFERENCES animal(id) ON DELETE CASCADE,
    CONSTRAINT fk_status_log_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE status_log_photo (
    id BIGSERIAL PRIMARY KEY,
    photo_id BIGINT,
    animal_status_log_id BIGINT,
    CONSTRAINT fk_status_log_photo_photo FOREIGN KEY (photo_id) REFERENCES photo(id) ON DELETE SET NULL,
    CONSTRAINT fk_status_log_photo_statuslog FOREIGN KEY (animal_status_log_id) REFERENCES animal_status_log(id) ON DELETE CASCADE
);

CREATE TABLE status_log_document (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT,
    animal_status_log_id BIGINT,
    CONSTRAINT fk_status_log_document_document FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE SET NULL,
    CONSTRAINT fk_status_log_document_statuslog FOREIGN KEY (animal_status_log_id) REFERENCES animal_status_log(id) ON DELETE CASCADE
);

CREATE TABLE animal_parameter_history (
    id BIGSERIAL PRIMARY KEY,
    recorded_at DATE,
    old_mass NUMERIC,
    new_mass NUMERIC,
    old_height NUMERIC,
    new_height NUMERIC,
    old_temperature NUMERIC,
    new_temperature NUMERIC,
    old_activity_level INT,
    new_activity_level INT,
    old_appetite_level INT,
    new_appetite_level INT,
    animal_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status_log_id BIGINT,
    CONSTRAINT fk_param_history_animal FOREIGN KEY (animal_id) REFERENCES animal(id) ON DELETE CASCADE,
    CONSTRAINT fk_param_history_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_param_history_statuslog FOREIGN KEY (status_log_id) REFERENCES animal_status_log(id) ON DELETE SET NULL
);
