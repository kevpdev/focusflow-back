--
-- PostgreSQL database dump
--

-- Dumped from database version 17.0
-- Dumped by pg_dump version 17.0

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
-- Data for Name: users; Type: TABLE DATA; Schema: admin; Owner: focusflow_app
--

INSERT INTO admin.users (id, email, password, created_at, username) VALUES
(1, 'dev.dragonpirce@gmail.com', '$2a$06$zyBM6X6p2RMOPY6l7qCGEO1.IshXqGjICmFOF2RCAhj8E4rJiW8dC', '2024-10-01 21:00:00.299206', 'devdragon'),
(2, 'toto@gmail.com', '$2a$10$VHIwtVnxxxLiv0KRUcG5rulhLoaDxsd3V22zECALi.qMT6E9Yj/ju', '2024-10-03 21:22:02.673546', 'toto74');



--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: admin; Owner: focusflow_app
--

SELECT pg_catalog.setval('admin.users_id_seq', 2, true);


--
-- PostgreSQL database dump complete
--

