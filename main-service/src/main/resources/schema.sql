CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR NOT NULL UNIQUE,
    name  VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(2000)                                       NOT NULL,
    category_id        BIGINT REFERENCES categories (id) ON DELETE CASCADE NOT NULL,
    created_on         TIMESTAMP                                           NOT NULL,
    description        VARCHAR(7000)                                       NOT NULL,
    event_date         TIMESTAMP                                           NOT NULL,
    location_id        BIGINT REFERENCES locations (id) ON DELETE CASCADE  NOT NULL,
    paid               BOOLEAN DEFAULT false                               NOT NULL,
    participant_limit  INT     DEFAULT 0                                   NOT NULL,
    published_on       TIMESTAMP,
    state              VARCHAR DEFAULT 'PENDING'                           NOT NULL CHECK (state IN ('PENDING', 'PUBLISHED', 'CANCELED')),
    request_moderation BOOLEAN DEFAULT true                                NOT NULL,
    title              VARCHAR(170)                                        NOT NULL,
    initiator_id       BIGINT REFERENCES users (id)                        NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN DEFAULT false NOT NULL,
    title varchar(50)           NOT NULL
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id     BIGINT REFERENCES events (id) ON DELETE CASCADE NOT NULL,
    requester_id BIGINT REFERENCES users (id) ON DELETE CASCADE  NOT NULL,
    status       VARCHAR DEFAULT 'PENDING'                       NOT NULL CHECK ( status IN ('PENDING', 'REJECTED', 'CONFIRMED', 'CANCELED')),
    created      TIMESTAMP                                       NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_events
(
    compilation_id BIGINT NOT NULL,
    event_id       BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text            VARCHAR(7000)                   NOT NULL,
    event_id        BIGINT REFERENCES events (id)   ON DELETE CASCADE NOT NULL,
    author_id       BIGINT REFERENCES users (id)    ON DELETE CASCADE NOT NULL,
    created_on      TIMESTAMP                       NOT NULL,
    published_on    TIMESTAMP,
    state           VARCHAR DEFAULT 'PENDING'       NOT NULL CHECK (state IN ('PENDING', 'PUBLISHED', 'CANCELED'))
);