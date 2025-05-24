CREATE DATABASE IF NOT EXISTS vulnshop;
USE vulnshop;

DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products; 
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    is_admin TINYINT(1) DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description TEXT,
    stock INT DEFAULT 0,
    image_url VARCHAR(255),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    total_amount DECIMAL(10,2) NOT NULL,
    status INT DEFAULT 0, -- 0: 待支付, 1: 已支付, 2: 已发货, 3: 已完成, 4: 已取消
    payment_method VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE cart_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    product_id INT,
    quantity INT DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

INSERT INTO users (username, password, email, is_admin) VALUES 
('admin', 'admin123', 'admin@example.com', 1),
('test', 'test123', 'test@example.com', 0),
('user1', 'pass123', 'user1@example.com', 0),
('user2', 'pass123', 'user2@example.com', 0);

INSERT INTO products (name, price, description, stock, image_url) VALUES 
('智能手机', 2999.00, '最新款智能手机，性能强劲，拍照清晰。', 100, '/images/products/phone.jpg'),
('笔记本电脑', 5999.00, '轻薄便携的笔记本电脑，适合办公和学习使用。', 50, '/images/products/laptop.jpg'),
('无线耳机', 299.00, '高音质无线蓝牙耳机，续航时间长。', 200, '/images/products/earphone.jpg'),
('高级钢笔', 199.00, '高品质钢笔，书写流畅，送礼佳品。', 80, '/images/products/pen.jpg'),
('琥珀色精油瓶', 99.00, '天然琥珀色精油瓶，古典美观，实用性强。', 120, '/images/products/oil.jpg'),
('黄金首饰套装', 3999.00, '纯金打造的首饰套装，含项链、手链、耳环。', 20, '/images/products/jewelry.jpg'),
('工装靴', 499.00, '耐磨防滑工装靴，经久耐用，适合户外活动。', 60, '/images/products/boots.jpg');

INSERT INTO orders (user_id, total_amount, status, payment_method) VALUES 
(2, 2999.00, 1, '支付宝'),
(3, 6298.00, 2, '微信支付'),
(4, 299.00, 0, '银行卡'),
(2, 5999.00, 3, '支付宝');

INSERT INTO cart_items (user_id, product_id, quantity) VALUES 
(2, 3, 1),
(3, 1, 1),
(3, 2, 1),
(4, 3, 2);