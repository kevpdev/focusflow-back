-- Création des schémas
CREATE SCHEMA admin;
CREATE SCHEMA users;

-- Activation de l'extension pgcrypto (utile pour le hachage des mots de passe, UUID, etc.)
CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;

-- Création des tables avec auto-incrémentation directement dans la colonne id
CREATE TABLE admin.users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    username VARCHAR(255)
);

CREATE TABLE users.focus_logs (
    id BIGSERIAL PRIMARY KEY,
    session_id INTEGER,
    event_type VARCHAR(50) NOT NULL,
    event_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users.focus_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    session_start TIMESTAMP WITH TIME ZONE NOT NULL,
    session_end TIMESTAMP WITH TIME ZONE,
    task_id BIGINT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(255) DEFAULT 'IN_PROGRESS'
);

CREATE TABLE users.notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users.tasks (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    priority INTEGER DEFAULT 1,
    due_date TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Création des index
CREATE INDEX idx_focus_logs_session_id ON users.focus_logs (session_id);
CREATE INDEX idx_focus_sessions_user_id ON users.focus_sessions (user_id);
CREATE INDEX idx_notifications_user_id ON users.notifications (user_id);
CREATE INDEX idx_tasks_user_id ON users.tasks (user_id);

-- Ajout des clés étrangères
ALTER TABLE users.focus_logs ADD CONSTRAINT focus_logs_session_id_fkey
    FOREIGN KEY (session_id) REFERENCES users.focus_sessions(id);

ALTER TABLE users.focus_sessions ADD CONSTRAINT focus_sessions_task_id_fkey
    FOREIGN KEY (task_id) REFERENCES users.tasks(id);

ALTER TABLE users.focus_sessions ADD CONSTRAINT focus_sessions_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES admin.users(id);

ALTER TABLE users.notifications ADD CONSTRAINT notifications_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES admin.users(id);

ALTER TABLE users.tasks ADD CONSTRAINT tasks_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES admin.users(id);
