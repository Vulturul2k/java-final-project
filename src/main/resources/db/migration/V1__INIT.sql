CREATE SCHEMA IF NOT EXISTS project;
SET search_path = project, pg_catalog;

CREATE SEQUENCE IF NOT EXISTS roles_seq START WITH 3 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS users (
                                     id uuid PRIMARY KEY,
                                     username TEXT,
                                     email TEXT,
                                     password TEXT
);

CREATE TABLE IF NOT EXISTS roles (
                                     id INTEGER PRIMARY KEY,
                                     name TEXT
);

CREATE TABLE IF NOT EXISTS user_role (
                                         user_id UUID NOT NULL,
                                         role_id INTEGER NOT NULL,
                                         PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES project.users (id),
    FOREIGN KEY (role_id) REFERENCES project.roles (id)
    );

INSERT INTO roles (id, name) VALUES
                                 (1, 'ADMIN'),
                                 (2, 'USER')
    ON CONFLICT (id) DO NOTHING;

CREATE TABLE IF NOT EXISTS book (
                                    id UUID PRIMARY KEY,
                                    title TEXT NOT NULL,
                                    author TEXT NOT NULL,
                                    owner_id UUID,
                                    FOREIGN KEY (owner_id) REFERENCES project.users(id)
    );



CREATE TABLE IF NOT EXISTS offer (
                                     id UUID PRIMARY KEY,
                                     sender_id UUID NOT NULL,
                                     receiver_id UUID NOT NULL,
                                     status TEXT NOT NULL DEFAULT 'PENDING',
                                     created_at TIMESTAMP DEFAULT now(),
    FOREIGN KEY (sender_id) REFERENCES project.users(id),
    FOREIGN KEY (receiver_id) REFERENCES project.users(id)
    );

CREATE TABLE IF NOT EXISTS offered_book (
                                            id UUID PRIMARY KEY,
                                            offer_id UUID NOT NULL,
                                            book_id UUID NOT NULL,
                                            is_requested BOOLEAN NOT NULL,
                                            FOREIGN KEY (offer_id) REFERENCES project.offer(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES project.book(id)
    );
