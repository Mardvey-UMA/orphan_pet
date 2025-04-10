CREATE TABLE _user (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       vk_id BIGINT,
                       birth_date DATE,
                       age INTEGER,
                       gender VARCHAR(10),
                       city VARCHAR(255),
                       country VARCHAR(255),
                       job VARCHAR(255),
                       education VARCHAR(255),
                       about_me TEXT,
                       theme VARCHAR(255) NOT NULL,
                       chat_id VARCHAR(255) DEFAULT NULL,
                       telegram_id VARCHAR(255) DEFAULT NULL
);

CREATE TABLE _photos (
                         id SERIAL PRIMARY KEY,
                         username VARCHAR(255) NOT NULL,
                         photo_url TEXT NOT NULL,
                         object_key TEXT NOT NULL,
                         mimetype VARCHAR(255) NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT fk_photos_user
                             FOREIGN KEY (username)
                                 REFERENCES _user(username)
                                 ON DELETE CASCADE
);



