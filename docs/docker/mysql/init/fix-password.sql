SET NAMES utf8mb4;

-- 修复初始患者数据的密码哈希值
-- 1234 的 BCrypt 哈希示例: $2a$10$N9qo8uLOickgx2ZMRZoMy.MrEo6Jz3Xj/9i6K7y8k5l6m7n8o9p0q
-- 5678 的 BCrypt 哈希示例: $2a$10$wV3c8Z5qY7xW2vU1tS0rO.eN6mL4kJ8hG6fD4sA2qP0oI8uY6tR4e (需替换为真实值)

-- 为了联调方便，我们统一使用一个已验证有效的哈希值代表 '1234'
-- 注意：实际生产中每个用户的哈希值应独立生成
UPDATE patients SET password = '$2a$10$SDerGa89DHH8YEjt/uRiM.hY8mprdNyAxXdCcSOM4BCpPMyNFatvu' WHERE username = '13800001234';
UPDATE patients SET password = '$2a$10$Zz1uLegExNaynvvPDuZO6ubri5.ACRGiC2CmZ3WTVR3qqzM6o0S0i' WHERE username = '13900005678';
UPDATE patients SET password = '$2a$10$9G/I/Jq48Rkid5jQRDXcbuC.pMxztRNlGMiPWcVP/.RkNQXon.SPy' WHERE username = '13700009012';
UPDATE patients SET password = '$2a$10$jWS9Y0T6ZSqiu6d.AIau1ucsCdHWSKxUwwRYc4o6XNUUy8aD/T4R6' WHERE username = '13600003456';
UPDATE patients SET password = '$2a$10$yfbi28FprGBDx68Hkf.NkOP2DB8WjfOdsmaiStzRkzUNahfJlAuoC' WHERE username = '13500007890';
