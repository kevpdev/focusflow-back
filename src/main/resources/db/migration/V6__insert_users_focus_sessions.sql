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
-- Data for Name: focus_sessions; Type: TABLE DATA; Schema: users; Owner: focusflow_app
--

INSERT INTO users.focus_sessions (id, user_id, session_start, session_end, task_id, created_at, updated_at, status) VALUES
(2, 1, '2024-10-22 20:22:23.075998', NULL, 2, '2024-10-22 20:22:23.075998', '2024-10-22 20:22:23.075998', 'IN_PROGRESS'),
(4, 1, '2024-10-26 19:13:39.158275', NULL, 3, '2024-10-26 19:13:39.158275', '2024-10-26 19:13:39.158275', 'IN_PROGRESS'),
(3, 1, '2024-10-24 18:39:12.148612', NULL, 3, '2024-10-24 18:39:12.148612', '2024-10-26 19:15:13.967702', 'PENDING');


--
-- Name: focus_sessions_id_seq; Type: SEQUENCE SET; Schema: users; Owner: focusflow_app
--

SELECT pg_catalog.setval('users.focus_sessions_id_seq', 4, true);


--
-- PostgreSQL database dump complete
--

