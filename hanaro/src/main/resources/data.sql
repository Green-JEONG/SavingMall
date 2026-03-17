-- 1. Users
INSERT INTO user (email, password, nickname, phoneNumber, role, createdAt) VALUES
('hanaro', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '관리자', '01000000000', 'ROLE_ADMIN', NOW()),
('user1@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '호록이', '01011110001', 'ROLE_USER', NOW()),
('user2@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '테스터2', '01011110002', 'ROLE_USER', NOW()),
('user3@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '테스터3', '01011110003', 'ROLE_USER', NOW()),
('user4@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '테스터4', '01011110004', 'ROLE_USER', NOW()),
('user5@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '테스터5', '01011110005', 'ROLE_USER', NOW()),
('user6@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '테스터6', '01011110006', 'ROLE_USER', NOW()),
('user7@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '테스터7', '01011110007', 'ROLE_USER', NOW()),
('user8@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '테스터8', '01011110008', 'ROLE_USER', NOW()),
('user9@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '테스터9', '01011110009', 'ROLE_USER', NOW()),
('user10@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '테스터10', '01011110010', 'ROLE_USER', NOW());

-- 2. Products
INSERT INTO product (name, type, paymentAmount, savingsCycle, periodMonths, maturityRate, terminationRate, imagePath) VALUES
('하나 정기예금', 'DEPOSIT', 5000000.00, NULL, 12, 3.50, 1.20, '/upload/20260316/sample1.png'),
('내맘대로 적금', 'SAVINGS', 300000.00, 'WEEKLY', 24, 4.20, 1.80, '/upload/20260316/sample2.png'),
('청년도약계좌', 'SAVINGS', 700000.00, 'MONTHLY', 60, 6.00, 2.50, '/upload/20260316/sample3.png'),
('시니어우대예금', 'DEPOSIT', 3000000.00, NULL, 36, 3.80, 1.50, '/upload/20260316/sample4.png'),
('군인든든적금', 'SAVINGS', 400000.00, 'MONTHLY', 18, 5.50, 2.20, '/upload/20260316/sample5.png'),
('첫거래적금', 'SAVINGS', 200000.00, 'WEEKLY', 6, 5.00, 2.00, '/upload/20260316/sample6.png'),
('직장인예금', 'DEPOSIT', 2000000.00, NULL, 12, 2.50, 1.00, '/upload/20260316/sample7.png'),
('디지털예금', 'DEPOSIT', 1000000.00, NULL, 12, 3.70, 1.30, '/upload/20260316/sample8.png'),
('다자녀행복적금', 'SAVINGS', 250000.00, 'MONTHLY', 12, 4.50, 1.90, '/upload/20260316/sample9.png'),
('정기적금V2', 'SAVINGS', 150000.00, 'WEEKLY', 24, 4.00, 1.70, '/upload/20260316/sample10.png');

-- 3. Accounts
INSERT INTO account (accountNumber, accountNumberFormatted, user_id, balance, accountType) VALUES
('12345678901', '123-4567-8901', 2, 1000000.00, 'FREE'),
('12345678902', '123-4567-8902', 3, 2500000.00, 'FREE'),
('12345678903', '123-4567-8903', 4, 3200000.00, 'FREE'),
('12345678904', '123-4567-8904', 5, 850000.00, 'FREE'),
('12345678905', '123-4567-8905', 6, 1450000.00, 'FREE'),
('12345678906', '123-4567-8906', 7, 980000.00, 'FREE'),
('12345678907', '123-4567-8907', 8, 4100000.00, 'FREE'),
('12345678908', '123-4567-8908', 9, 530000.00, 'FREE'),
('12345678909', '123-4567-8909', 10, 2750000.00, 'FREE'),
('12345678910', '123-4567-8910', 11, 620000.00, 'FREE'),
('20100010001', '201-0001-0001', 2, 5000000.00, 'DEPOSIT'),
('20200010001', '202-0001-0001', 3, 300000.00, 'SAVINGS'),
('20300010001', '203-0001-0001', 4, 700000.00, 'SAVINGS'),
('20400010001', '204-0001-0001', 5, 3095000.00, 'DEPOSIT'),
('20500010001', '205-0001-0001', 6, 418500.00, 'SAVINGS'),
('20600010001', '206-0001-0001', 7, 0.00, 'SAVINGS'),
('20800010001', '208-0001-0001', 8, 150000.00, 'DEPOSIT');

-- 4. Subscriptions
INSERT INTO subscription (user_id, product_id, account_id, joinedAt, maturityAt, status, accumulatedInterest) VALUES
 (2, 1, 11, '2026-01-01', '2027-01-01', 'ACTIVE', 0.00),
 (3, 2, 12, '2025-10-01', '2027-10-01', 'ACTIVE', 0.00),
 (4, 3, 13, '2025-01-15', '2030-01-15', 'ACTIVE', 0.00),
 (5, 4, 14, '2023-03-01', '2026-03-01', 'MATURED', 95000.00),
 (6, 5, 15, '2025-06-01', '2026-12-01', 'TERMINATED', 18500.00),
 (7, 6, 16, '2025-12-20', '2026-06-20', 'ACTIVE', 0.00),
 (8, 8, 17, '2025-04-10', '2026-04-10', 'ACTIVE', 0.00);

-- 5. Transactions
INSERT INTO transaction (subscription_id, type, amount, description, createdAt) VALUES
 (1, 'SUBSCRIBE', 5000000.00, '하나 정기예금 가입', NOW()),
 (2, 'SUBSCRIBE', 300000.00, '내맘대로 적금 가입', NOW()),
 (3, 'SUBSCRIBE', 700000.00, '청년도약계좌 가입', NOW()),
 (4, 'TERMINATE', 95000.00, '시니어우대예금 만기 처리', NOW()),
 (5, 'TERMINATE', 18500.00, '군인든든적금 중도 해지', NOW()),
 (7, 'TRANSFER', 150000.00, '상품 계좌 이체', NOW());
