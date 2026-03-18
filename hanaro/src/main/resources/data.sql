INSERT INTO `user` (`email`, `password`, `nickname`, `phoneNumber`, `role`, `createdAt`) VALUES
('hanaro', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '하나관리자', '01090000000', 'ROLE_ADMIN', '2026-01-02 09:00:00'),
('minji@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '민지', '01031001111', 'ROLE_USER', '2026-01-03 10:15:00'),
('junho@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '준호', '01031002222', 'ROLE_USER', '2026-01-05 14:20:00'),
('seoyeon@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '서연', '01031003333', 'ROLE_USER', '2026-01-08 11:05:00'),
('doyun@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '도윤', '01031004444', 'ROLE_USER', '2026-01-12 16:40:00'),
('jiwoo@test.com', '$2y$10$YfGDGKBQexj2WoWLJABHneZMdW1nWR.xmGsIC8Neo50v0UcAQ.FtK', '지우', '01031005555', 'ROLE_USER', '2026-01-15 13:35:00');

--

INSERT INTO `product` (`name`, `type`, `paymentAmount`, `savingsCycle`, `periodMonths`, `maturityRate`, `terminationRate`, `imagePath`) VALUES
('하나 프리미엄 정기예금', 'DEPOSIT', 5000000.00, NULL, 12, 3.80, 1.30, '/upload/20260318/hana-premium-deposit.svg'),
('급여우대 정기적금', 'SAVINGS', 300000.00, 'MONTHLY', 12, 4.60, 1.80, '/upload/20260318/salary-plus-savings.svg'),
('청년 도약 적금', 'SAVINGS', 700000.00, 'MONTHLY', 24, 5.20, 2.00, '/upload/20260318/youth-jump-savings.svg'),
('주간 챌린지 적금', 'SAVINGS', 100000.00, 'WEEKLY', 6, 4.10, 1.50, '/upload/20260318/weekly-challenge-savings.svg'),
('시니어 안심 예금', 'DEPOSIT', 3000000.00, NULL, 24, 3.60, 1.20, '/upload/20260318/senior-care-deposit.svg'),
('첫거래 웰컴 예금', 'DEPOSIT', 1000000.00, NULL, 6, 3.20, 0.90, '/upload/20260318/first-welcome-deposit.svg');

--

INSERT INTO `account` (`accountNumber`, `accountNumberFormatted`, `user_id`, `balance`, `accountType`) VALUES
('10010000001', '100-1000-0001', 1, 15000000.00, 'FREE'),
('10010000002', '100-1000-0002', 2, 4200000.00, 'FREE'),
('10010000003', '100-1000-0003', 3, 8600000.00, 'FREE'),
('10010000004', '100-1000-0004', 4, 2100000.00, 'FREE'),
('10010000005', '100-1000-0005', 5, 5700000.00, 'FREE'),
('10010000006', '100-1000-0006', 6, 1350000.00, 'FREE'),
('20160000001', '201-6000-0001', 2, 900000.00, 'SAVINGS'),
('30160000001', '301-6000-0001', 3, 5000000.00, 'DEPOSIT'),
('30160000002', '301-6000-0002', 4, 3216000.00, 'DEPOSIT'),
('20160000002', '201-6000-0002', 5, 748200.00, 'SAVINGS'),
('20160000003', '201-6000-0003', 6, 300000.00, 'SAVINGS');

--

INSERT INTO `subscription` (`user_id`, `product_id`, `account_id`, `joinedAt`, `maturityAt`, `status`, `accumulatedInterest`) VALUES
(2, 2, 7, '2025-12-10', '2026-12-10', 'ACTIVE', 11850.00),
(3, 1, 8, '2025-09-01', '2026-09-01', 'ACTIVE', 91200.00),
(4, 5, 9, '2024-03-01', '2026-03-01', 'MATURED', 216000.00),
(5, 3, 10, '2025-04-15', '2027-04-15', 'TERMINATED', 48200.00),
(6, 4, 11, '2026-02-01', '2026-08-01', 'ACTIVE', 3620.00);

--

INSERT INTO `transaction` (`subscription_id`, `type`, `amount`, `description`, `createdAt`) VALUES
(1, 'SUBSCRIBE', 300000.00, '급여우대 정기적금 가입', '2025-12-10 09:30:00'),
(1, 'TRANSFER', 600000.00, '급여 계좌에서 추가 납입', '2026-02-10 08:20:00'),
(2, 'SUBSCRIBE', 5000000.00, '하나 프리미엄 정기예금 가입', '2025-09-01 11:10:00'),
(3, 'SUBSCRIBE', 3000000.00, '시니어 안심 예금 가입', '2024-03-01 15:40:00'),
(3, 'TERMINATE', 216000.00, '시니어 안심 예금 만기 처리', '2026-03-01 09:00:00'),
(4, 'SUBSCRIBE', 700000.00, '청년 도약 적금 가입', '2025-04-15 10:05:00'),
(4, 'TERMINATE', 48200.00, '청년 도약 적금 중도 해지', '2026-02-14 17:25:00'),
(5, 'SUBSCRIBE', 100000.00, '주간 챌린지 적금 가입', '2026-02-01 12:00:00'),
(5, 'TRANSFER', 200000.00, '챌린지 적금 추가 납입', '2026-03-08 18:10:00');
