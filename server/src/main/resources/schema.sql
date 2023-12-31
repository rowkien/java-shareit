DROP TABLE IF EXISTS users, items, bookings, comments, requests;

CREATE TABLE IF NOT EXISTS users
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    description VARCHAR(255) NOT NULL,
    requestor_id INT REFERENCES users (id),
    created TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS items
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    owner_id INT REFERENCES users (id),
    request_id INT REFERENCES requests (id),
    available BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS bookings
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    start_time TIMESTAMP WITHOUT TIME ZONE,
    end_time TIMESTAMP WITHOUT TIME ZONE,
    item_id INT REFERENCES items (id) ON DELETE CASCADE,
    booker_id INT REFERENCES users (id) ON DELETE CASCADE,
    booking_status VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    text VARCHAR(255) NOT NULL,
    item_id INT REFERENCES items (id) ON DELETE CASCADE,
    author_id INT REFERENCES users (id) ON DELETE CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE
);