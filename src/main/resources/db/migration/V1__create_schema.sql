--
-- PostgreSQL database dump
--

-- Dumped from database version 16.0
-- Dumped by pg_dump version 16.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: admin; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA admin;


--ALTER SCHEMA admin OWNER TO postgres;

--
-- Name: users; Type: SCHEMA; Schema: -; Owner: focusflow_app
--

CREATE SCHEMA users;


--ALTER SCHEMA users OWNER TO focusflow_app;

--
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;

--
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: roles; Type: TABLE; Schema: admin; Owner: focusflow_app
--

CREATE TABLE admin.roles (
    id bigint NOT NULL,
    name character varying(255) NOT NULL
);


--ALTER TABLE admin.roles OWNER TO focusflow_app;

--
-- Name: roles_id_seq; Type: SEQUENCE; Schema: admin; Owner: focusflow_app
--

CREATE SEQUENCE admin.roles_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


-- ALTER SEQUENCE admin.roles_id_seq OWNER TO focusflow_app;

--
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: admin; Owner: focusflow_app
--

-- ALTER SEQUENCE admin.roles_id_seq OWNED BY admin.roles.id;


--
-- Name: roles_seq; Type: SEQUENCE; Schema: admin; Owner: focusflow_app
--

CREATE SEQUENCE admin.roles_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER SEQUENCE admin.roles_seq OWNER TO focusflow_app;

--
-- Name: user_roles; Type: TABLE; Schema: admin; Owner: focusflow_app
--

CREATE TABLE admin.user_roles (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


--ALTER TABLE admin.user_roles OWNER TO focusflow_app;

--
-- Name: users; Type: TABLE; Schema: admin; Owner: focusflow_app
--

CREATE TABLE admin.users (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    username character varying(255)
);


--ALTER TABLE admin.users OWNER TO focusflow_app;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: admin; Owner: focusflow_app
--

CREATE SEQUENCE admin.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER SEQUENCE admin.users_id_seq OWNER TO focusflow_app;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: admin; Owner: focusflow_app
--

--ALTER SEQUENCE admin.users_id_seq OWNED BY admin.users.id;


--
-- Name: focus_logs; Type: TABLE; Schema: users; Owner: focusflow_app
--

CREATE TABLE users.focus_logs (
    id integer NOT NULL,
    session_id integer,
    event_type character varying(50) NOT NULL,
    event_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--ALTER TABLE users.focus_logs OWNER TO focusflow_app;

--
-- Name: focus_logs_id_seq; Type: SEQUENCE; Schema: users; Owner: focusflow_app
--

CREATE SEQUENCE users.focus_logs_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER SEQUENCE users.focus_logs_id_seq OWNER TO focusflow_app;

--
-- Name: focus_logs_id_seq; Type: SEQUENCE OWNED BY; Schema: users; Owner: focusflow_app
--

--ALTER SEQUENCE users.focus_logs_id_seq OWNED BY users.focus_logs.id;


--
-- Name: focus_sessions; Type: TABLE; Schema: users; Owner: focusflow_app
--

CREATE TABLE users.focus_sessions (
    id bigint NOT NULL,
    user_id bigint,
    session_start timestamp without time zone NOT NULL,
    session_end timestamp without time zone,
    task_id bigint,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    status character varying(255) DEFAULT 'IN_PROGRESS'::character varying
);


--ALTER TABLE users.focus_sessions OWNER TO focusflow_app;

--
-- Name: focus_sessions_id_seq; Type: SEQUENCE; Schema: users; Owner: focusflow_app
--

CREATE SEQUENCE users.focus_sessions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER SEQUENCE users.focus_sessions_id_seq OWNER TO focusflow_app;

--
-- Name: focus_sessions_id_seq; Type: SEQUENCE OWNED BY; Schema: users; Owner: focusflow_app
--

--ALTER SEQUENCE users.focus_sessions_id_seq OWNED BY users.focus_sessions.id;


--
-- Name: notifications; Type: TABLE; Schema: users; Owner: focusflow_app
--

CREATE TABLE users.notifications (
    id integer NOT NULL,
    user_id integer,
    message text NOT NULL,
    notification_type character varying(50) NOT NULL,
    read boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--ALTER TABLE users.notifications OWNER TO focusflow_app;

--
-- Name: notifications_id_seq; Type: SEQUENCE; Schema: users; Owner: focusflow_app
--

CREATE SEQUENCE users.notifications_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER SEQUENCE users.notifications_id_seq OWNER TO focusflow_app;

--
-- Name: notifications_id_seq; Type: SEQUENCE OWNED BY; Schema: users; Owner: focusflow_app
--

--ALTER SEQUENCE users.notifications_id_seq OWNED BY users.notifications.id;


--
-- Name: tasks; Type: TABLE; Schema: users; Owner: focusflow_app
--

CREATE TABLE users.tasks (
    id bigint NOT NULL,
    user_id bigint,
    title character varying(255) NOT NULL,
    description text,
    status character varying(50) DEFAULT 'PENDING'::character varying,
    priority integer DEFAULT 1,
    due_date timestamp with time zone,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


--ALTER TABLE users.tasks OWNER TO focusflow_app;

--
-- Name: tasks_id_seq; Type: SEQUENCE; Schema: users; Owner: focusflow_app
--

CREATE SEQUENCE users.tasks_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER SEQUENCE users.tasks_id_seq OWNER TO focusflow_app;

--
-- Name: tasks_id_seq; Type: SEQUENCE OWNED BY; Schema: users; Owner: focusflow_app
--

--ALTER SEQUENCE users.tasks_id_seq OWNED BY users.tasks.id;


--
-- Name: roles id; Type: DEFAULT; Schema: admin; Owner: focusflow_app
--

ALTER TABLE ONLY admin.roles ALTER COLUMN id SET DEFAULT nextval('admin.roles_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: admin; Owner: focusflow_app
--

ALTER TABLE ONLY admin.users ALTER COLUMN id SET DEFAULT nextval('admin.users_id_seq'::regclass);


--
-- Name: focus_logs id; Type: DEFAULT; Schema: users; Owner: focusflow_app
--

ALTER TABLE ONLY users.focus_logs ALTER COLUMN id SET DEFAULT nextval('users.focus_logs_id_seq'::regclass);


--
-- Name: focus_sessions id; Type: DEFAULT; Schema: users; Owner: focusflow_app
--

ALTER TABLE ONLY users.focus_sessions ALTER COLUMN id SET DEFAULT nextval('users.focus_sessions_id_seq'::regclass);


--
-- Name: notifications id; Type: DEFAULT; Schema: users; Owner: focusflow_app
--

ALTER TABLE ONLY users.notifications ALTER COLUMN id SET DEFAULT nextval('users.notifications_id_seq'::regclass);


--
-- Name: tasks id; Type: DEFAULT; Schema: users; Owner: focusflow_app
--

ALTER TABLE ONLY users.tasks ALTER COLUMN id SET DEFAULT nextval('users.tasks_id_seq'::regclass);


--
-- Name: roles roles_name_key; Type: CONSTRAINT; Schema: admin; Owner: focusflow_app
--

ALTER TABLE ONLY admin.roles
    ADD CONSTRAINT roles_name_key UNIQUE (name);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: admin; Owner: focusflow_app
--

ALTER TABLE ONLY admin.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: user_roles user_roles_pkey; Type: CONSTRAINT; Schema: admin; Owner: focusflow_app
--

ALTER TABLE ONLY admin.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: admin; Owner: focusflow_app
--

ALTER TABLE ONLY admin.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: admin; Owner: focusflow_app
--

ALTER TABLE ONLY admin.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: focus_logs focus_logs_pkey; Type: CONSTRAINT; Schema: users; Owner: focusflow_app
--

ALTER TABLE ONLY users.focus_logs
    ADD CONSTRAINT focus_logs_pkey PRIMARY KEY (id);


--
-- Name: focus_sessions focus_sessions_pkey; Type: CONSTRAINT; Schema: users; Owner: focusflow_app
--

ALTER TABLE ONLY users.focus_sessions
    ADD CONSTRAINT focus_sessions_pkey PRIMARY KEY (id);


--
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: users; Owner: focusflow_app
--

ALTER TABLE ONLY users.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- Name: tasks tasks_pkey; Type: CONSTRAINT; Schema: users; Owner: focusflow_app
--

ALTER TABLE ONLY users.tasks
    ADD CONSTRAINT tasks_pkey PRIMARY KEY (id);


--
-- Name: idx_focus_logs_session_id; Type: INDEX; Schema: users; Owner: focusflow_app
--

CREATE INDEX idx_focus_logs_session_id ON users.focus_logs USING btree (session_id);


--
-- Name: idx_focus_sessions_user_id; Type: INDEX; Schema: users; Owner: focusflow_app
--

CREATE INDEX idx_focus_sessions_user_id ON users.focus_sessions USING btree (user_id);


--
-- Name: idx_notifications_user_id; Type: INDEX; Schema: users; Owner: focusflow_app
--

CREATE INDEX idx_notifications_user_id ON users.notifications USING btree (user_id);


--
-- Name: idx_tasks_user_id; Type: INDEX; Schema: users; Owner: focusflow_app
--

CREATE INDEX idx_tasks_user_id ON users.tasks USING btree (user_id);


--
-- Name: user_roles user_roles_role_id_fkey; Type: FK CONSTRAINT; Schema: admin; Owner: focusflow_app
--

ALTER TABLE ONLY admin.user_roles
    ADD CONSTRAINT user_roles_role_id_fkey FOREIGN KEY (role_id) REFERENCES admin.roles(id);


--
-- Name: user_roles user_roles_user_id_fkey; Type: FK CONSTRAINT; Schema: admin; Owner: focusflow_app
--

ALTER TABLE ONLY admin.user_roles
    ADD CONSTRAINT user_roles_user_id_fkey FOREIGN KEY (user_id) REFERENCES admin.users(id);


--
-- Name: focus_logs focus_logs_session_id_fkey; Type: FK CONSTRAINT; Schema: users; Owner: focusflow_app
--

ALTER TABLE ONLY users.focus_logs
    ADD CONSTRAINT focus_logs_session_id_fkey FOREIGN KEY (session_id) REFERENCES users.focus_sessions(id);


--
-- Name: focus_sessions focus_sessions_task_id_fkey; Type: FK CONSTRAINT; Schema: users; Owner: focusflow_app
--

ALTER TABLE ONLY users.focus_sessions
    ADD CONSTRAINT focus_sessions_task_id_fkey FOREIGN KEY (task_id) REFERENCES users.tasks(id);


--
-- Name: focus_sessions focus_sessions_user_id_fkey; Type: FK CONSTRAINT; Schema: users; Owner: focusflow_app
--

ALTER TABLE ONLY users.focus_sessions
    ADD CONSTRAINT focus_sessions_user_id_fkey FOREIGN KEY (user_id) REFERENCES admin.users(id);


--
-- Name: notifications notifications_user_id_fkey; Type: FK CONSTRAINT; Schema: users; Owner: focusflow_app
--

ALTER TABLE ONLY users.notifications
    ADD CONSTRAINT notifications_user_id_fkey FOREIGN KEY (user_id) REFERENCES admin.users(id);


--
-- Name: tasks tasks_user_id_fkey; Type: FK CONSTRAINT; Schema: users; Owner: focusflow_app
--

ALTER TABLE ONLY users.tasks
    ADD CONSTRAINT tasks_user_id_fkey FOREIGN KEY (user_id) REFERENCES admin.users(id);


--
-- Name: SCHEMA admin; Type: ACL; Schema: -; Owner: postgres
--

--GRANT ALL ON SCHEMA admin TO focusflow_app;




--
-- PostgreSQL database dump complete
--

