INSERT INTO admin.users (email, password, created_at, username) VALUES
('dev76@gmail.com', '$2a$06$zyBM6X6p2RMOPY6l7qCGEO1.IshXqGjICmFOF2RCAhj8E4rJiW8dC', '2024-10-01 21:00:00.299206', 'dev76'),
('toto@gmail.com', '$2a$10$VHIwtVnxxxLiv0KRUcG5rulhLoaDxsd3V22zECALi.qMT6E9Yj/ju', '2024-10-03 21:22:02.673546', 'toto74');

-- Resynchroniser la séquence avec le plus grand ID
SELECT setval('admin.users_id_seq', (SELECT MAX(id) FROM admin.users), true);
