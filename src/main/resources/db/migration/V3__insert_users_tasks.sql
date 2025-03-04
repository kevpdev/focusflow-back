
INSERT INTO users.tasks (user_id, title, description, status, priority, due_date, created_at, updated_at) VALUES
(1, 'Faire les courses', 'Acheter des légumes et du lait', 'DONE', 3, '2024-10-08 00:00:00+02', '2024-10-18 21:33:44.021104+02', '2024-10-18 21:33:44.021104+02'),
(2, 'Préparer la présentation', 'Préparer la présentation pour le client X', 'PENDING', 2, '2024-10-11 00:00:00+02', '2024-10-18 21:41:50.457473+02', '2024-10-18 21:41:50.457473+02'),
(2, 'Envoyer les factures', 'Envoyer les factures aux clients pour octobre', 'PENDING', 3, '2024-10-13 00:00:00+02', '2024-10-18 21:41:50.457473+02', '2024-10-18 21:41:50.457473+02'),
(2, 'Appeler le fournisseur', 'Appeler le fournisseur pour confirmer la livraison', 'PENDING', 1, '2024-10-14 00:00:00+02', '2024-10-18 21:41:50.457473+02', '2024-10-18 21:41:50.457473+02'),
(2, 'Finaliser le rapport', 'Finaliser le rapport de performance pour Q3', 'PENDING', 4, '2024-10-09 00:00:00+02', '2024-10-18 21:41:50.457473+02', '2024-10-18 21:41:50.457473+02'),
(2, 'Nettoyage du bureau', 'Nettoyer le bureau avant la visite du client', 'DONE', 2, '2024-10-07 00:00:00+02', '2024-10-18 21:41:50.457473+02', '2024-10-18 21:41:50.457473+02'),
(1, 'Faire la vaisselle', 'Faire la vaisselle avant midi', 'DONE', 1, '2024-10-10 00:00:00+02', '2024-10-18 21:33:44.021104+02', '2024-10-18 21:33:44.021104+02'),
(2, 'Organiser la réunion', 'Préparer la réunion hebdomadaire de l''équipe', 'PENDING', 1, '2024-10-15 00:00:00+02', '2024-10-18 21:33:44.021104+02', '2025-01-16 21:07:13.092364+01'),
(1, 'Réviser le code', 'Réviser le code du projet FocusFlow', 'IN_PROGRESS', 2, '2024-10-12 00:00:00+02', '2024-10-18 21:33:44.021104+02', '2025-01-16 20:13:11.573588+01'),
(1, 'Faire caca', 'Faire caca avant midi', 'PENDING', 1, '2025-01-29 00:00:00+01', '2025-01-15 20:53:10.660754+01', '2025-01-16 20:51:03.867765+01'),
(1, 'Organiser la réunion de crise', 'Organiser la réunion de crise suite à l''anomalie de production', 'PENDING', 3, '2025-01-16 00:00:00+01', '2025-01-16 20:17:39.756492+01', '2025-01-16 20:17:39.756492+01'),
(1, 'Ecrire une lettre', 'Ecrire une lettre pour Charles', 'IN_PROGRESS', 1, '2025-01-30 00:00:00+01', '2025-01-16 20:38:33.709964+01', '2025-01-16 20:38:33.709964+01'),
(1, 'Réparer le moniteur', 'Réparer le moniteur', 'PENDING', 2, '2025-01-30 00:00:00+01', '2025-01-16 21:17:06.484919+01', '2025-01-16 21:17:23.501353+01');

-- Resynchroniser la séquence avec le plus grand ID
SELECT setval('users.tasks_id_seq', (SELECT MAX(id) FROM users.tasks), true);