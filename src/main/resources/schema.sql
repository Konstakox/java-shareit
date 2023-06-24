drop table if exists users, items, bookings, comments;
-- drop type booking_status;

CREATE TABLE IF NOT EXISTS users
(
    user_id   INT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_name VARCHAR(64)                          NOT NULL,
    email     VARCHAR(32)                          NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT uq_user_email UNIQUE (email)
);
CREATE TABLE IF NOT EXISTS items
(
    item_id     INT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    item_name   VARCHAR(64)                          NOT NULL,
    description VARCHAR(256)                         NOT NULL,
    available   BOOLEAN                              NOT NULL,
    owner       INT                                  NOT NULL,
    request     INT,
    CONSTRAINT pk_items PRIMARY KEY (item_id),
    CONSTRAINT fk_owner_item FOREIGN KEY (owner) REFERENCES users (user_id) ON DELETE CASCADE
--     CONSTRAINT UQ_ITEM_OWNER UNIQUE (owner)
);
-- CREATE TYPE booking_status AS ENUM ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');
CREATE TABLE IF NOT EXISTS bookings
(
    booking_id     INT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date     TIMESTAMP WITHOUT TIME ZONE,
    end_date       TIMESTAMP WITHOUT TIME ZONE,
    item_id        INT                                  NOT NULL,
    booker_id      INT                                  NOT NULL,
    booking_status VARCHAR(64)                          NOT NULL,
    CONSTRAINT pk_booking_id PRIMARY KEY (booking_id),
    CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES items (item_id) ON DELETE CASCADE,
    CONSTRAINT fk_booker_id FOREIGN KEY (booker_id) REFERENCES users (user_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS comments
(
    comments_id INT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text        VARCHAR(511)                         NOT NULL,
    item_id     INT                                  NOT NULL,
    author_id   INT                                  NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_comments_id PRIMARY KEY (comments_id),
    CONSTRAINT fk_item_id2 FOREIGN KEY (item_id) REFERENCES items (item_id) ON DELETE CASCADE,
    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES users (user_id) ON DELETE CASCADE
);


