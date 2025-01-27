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
-- Data for Name: tasks; Type: TABLE DATA; Schema: users; Owner: focusflow_app
--

INSERT INTO users.tasks (id, user_id, title, description, status, priority, due_date, created_at, updated_at) VALUES
(4, 1, 'Faire les courses', 'Acheter des légumes et du lait', 'DONE', 3, '2024-10-08 00:00:00+02', '2024-10-18 21:33:44.021104+02', '2024-10-18 21:33:44.021104+02'),
(7, 2, 'Préparer la présentation', 'Préparer la présentation pour le client X', 'PENDING', 2, '2024-10-11 00:00:00+02', '2024-10-18 21:41:50.457473+02', '2024-10-18 21:41:50.457473+02'),
(8, 2, 'Envoyer les factures', 'Envoyer les factures aux clients pour octobre', 'PENDING', 3, '2024-10-13 00:00:00+02', '2024-10-18 21:41:50.457473+02', '2024-10-18 21:41:50.457473+02'),
(9, 2, 'Appeler le fournisseur', 'Appeler le fournisseur pour confirmer la livraison', 'PENDING', 1, '2024-10-14 00:00:00+02', '2024-10-18 21:41:50.457473+02', '2024-10-18 21:41:50.457473+02'),
(10, 2, 'Finaliser le rapport', 'Finaliser le rapport de performance pour Q3', 'PENDING', 4, '2024-10-09 00:00:00+02', '2024-10-18 21:41:50.457473+02', '2024-10-18 21:41:50.457473+02'),
(11, 2, 'Nettoyage du bureau', 'Nettoyer le bureau avant la visite du client', 'DONE', 2, '2024-10-07 00:00:00+02', '2024-10-18 21:41:50.457473+02', '2024-10-18 21:41:50.457473+02'),
(2, 1, 'Faire la vaisselle', 'Faire la vaisselle avant midi', 'DONE', 1, '2024-10-10 00:00:00+02', '2024-10-18 21:33:44.021104+02', '2024-10-18 21:33:44.021104+02'),
(5, NULL, 'Organiser la réunion', 'Préparer la réunion hebdomadaire de l''équipe', 'PENDING', 1, '2024-10-15 00:00:00+02', '2024-10-18 21:33:44.021104+02', '2025-01-16 21:07:13.092364+01'),
(3, 1, 'Réviser le code', 'Réviser le code du projet FocusFlow', 'IN_PROGRESS', 2, '2024-10-12 00:00:00+02', '2024-10-18 21:33:44.021104+02', '2025-01-16 20:13:11.573588+01'),
(12, 1, 'Faire caca', 'Faire caca avant midi', 'PENDING', 1, '2025-01-29 00:00:00+01', '2025-01-15 20:53:10.660754+01', '2025-01-16 20:51:03.867765+01'),
(13, 1, 'Organiser la réunion de crise', 'Organiser la réunion de crise suite à l''anomalie de production', 'PENDING', 3, '2025-01-16 00:00:00+01', '2025-01-16 20:17:39.756492+01', '2025-01-16 20:17:39.756492+01'),
(14, 1, 'Ecrire une lettre', 'Ecrire une lettre pour Charles', 'IN_PROGRESS', 1, '2025-01-30 00:00:00+01', '2025-01-16 20:38:33.709964+01', '2025-01-16 20:38:33.709964+01'),
(15, 1, 'Réparer le moniteur', 'Réparer le moniteur', 'PENDING', 2, '2025-01-30 00:00:00+01', '2025-01-16 21:17:06.484919+01', '2025-01-16 21:17:23.501353+01');



--
-- Name: tasks_id_seq; Type: SEQUENCE SET; Schema: users; Owner: focusflow_app
--

SELECT pg_catalog.setval('users.tasks_id_seq', 15, true);


--
-- PostgreSQL database dump complete
--

