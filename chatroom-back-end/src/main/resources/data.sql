INSERT INTO users (account, username, password, created_at) VALUES
('user01', 'Danny', 'aaa123', '2026-04-29 09:00:00'),
('user02', 'Amy', 'bbb456', '2026-04-29 09:05:00'),
('user03', 'Mike', 'ccc789', '2026-04-29 09:10:00'),
('user04', 'John', 'ddd101', '2026-04-29 09:15:00'),
('user05', 'Eva', 'eee202', '2026-04-29 09:20:00');

INSERT INTO chat_room (name, type, created_at, last_message_time) VALUES
('General Chat', 'GROUP', '2026-04-29 09:30:00', '2026-04-29 10:10:00'),
('Tech Talk', 'GROUP', '2026-04-29 09:35:00','2026-04-29 10:20:00'),
(null, 'PRIVATE', '2026-04-29 09:40:00', '2026-04-29 10:30:00'),
('Support', 'GROUP', '2026-04-29 09:45:00', '2026-04-29 10:40:00'),
('Gaming', 'GROUP', '2026-04-29 09:50:00', '2026-04-29 10:50:00');

INSERT INTO chat_room_member (room_id, user_id, joined_at, last_read_at) VALUES
(1, 1, '2026-04-29 10:00:00', '2026-04-29 10:01:00'), -- General Chat: Danny
(1, 2, '2026-04-29 10:05:00', '2026-04-30 10:00:00'), -- General Chat: Amy
(1, 3, '2026-04-29 10:10:00', '2026-04-30 10:00:00'), -- General Chat: Mike
(2, 3, '2026-04-29 10:15:00', '2026-04-30 10:00:00'), -- Tech Talk: Mike
(2, 1, '2026-04-29 10:20:00', '2026-04-30 10:00:00'), -- Tech Talk: Danny
(3, 1, '2026-04-29 10:25:00', '2026-04-29 10:28:00'), -- Private Chat: Danny
(3, 2, '2026-04-29 10:30:00', '2026-04-30 10:00:00'), -- Private Chat: Amy
(4, 4, '2026-04-29 10:35:00', '2026-04-30 10:00:00'), -- Support: John
(4, 5, '2026-04-29 10:40:00', '2026-04-30 10:00:00'), -- Support: Eva
(5, 5, '2026-04-29 10:45:00', '2026-04-30 10:00:00'), -- Gaming: Eva
(5, 2, '2026-04-29 10:50:00', '2026-04-30 10:00:00'); -- Gaming: Amy

INSERT INTO chat_message (room_id, sender_id, content, created_at, deleted_at) VALUES
(1, 1, 'Hello everyone!', '2026-04-29 10:00:00', null),
(1, 2, 'Hi Danny!', '2026-04-29 10:05:00', null),
(1, 2, 'How are you?', '2026-04-29 10:05:00', '2026-04-29 10:06:00'),
(1, 3, 'Anyone here into programming?', '2026-04-29 10:10:00', null),
(2, 3, 'What’s the best Java framework?', '2026-04-29 10:15:00', null),
(2, 1, 'Spring Boot is really popular right now.', '2026-04-29 10:20:00', null),
(3, 1, 'Hey Amy, how’s everything?', '2026-04-29 10:25:00', null),
(3, 2, 'Hey Danny! All good, just been busy with work.', '2026-04-29 10:30:00', null),
(4, 4, 'I need help with my account login.', '2026-04-29 10:35:00', null),
(4, 5, 'I can assist you with that. What seems to be the problem?', '2026-04-29 10:40:00', null),
(5, 5, 'Who’s playing the new game?', '2026-04-29 10:45:00', null),
(5, 2, 'I am! Let’s team up!', '2026-04-29 10:50:00', null);