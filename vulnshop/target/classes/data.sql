DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products; 
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE products (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE orders (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_orders_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (username, password) VALUES ('admin', 'admin123');
INSERT INTO users (username, password) VALUES ('user1', 'pass1');
INSERT INTO users (username, password) VALUES ('user2', 'pass2');

INSERT INTO products (name, price) VALUES ('iPhone 15', 6999.00);
INSERT INTO products (name, price) VALUES ('MacBook Pro', 15999.00);
INSERT INTO products (name, price) VALUES ('AirPods', 1299.00);

INSERT INTO orders (user_id, product_id, quantity) VALUES (1, 1, 1);
INSERT INTO orders (user_id, product_id, quantity) VALUES (2, 2, 2);
INSERT INTO orders (user_id, product_id, quantity) VALUES (3, 3, 3);