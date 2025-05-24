# vulnshop SQL注入靶场
如需运行项目，直接在vulnshop目录下执行mvn spring-boot:run即可
# 技术栈
maven3 
java23 
springboot
# 运行方式
cd vulnshop
mvn spring-boot:run
## 开发进度

### 2024-06-09
- 使用Spring Boot 3和Maven 3初始化了项目。
- 配置了`pom.xml`，集成Spring Boot、Spring Web、Spring Data JPA、H2数据库、Thymeleaf等依赖。
- 创建了`application.yml`，配置H2内存数据库、JPA、Thymeleaf和端口。
- 编写了`data.sql`，初始化了用户、商品、订单三张表及测试数据。
- 创建了Spring Boot主类`VulnshopApplication`。

### 2024-06-09（进度2）
- 创建了User、Product、Order实体类，分别对应users、products、orders三张表。
- 创建了UserRepository、ProductRepository、OrderRepository，基于Spring Data JPA实现数据访问。

### 2024-06-09（进度3）
- 创建了UserController，包含存在SQL注入漏洞的登录接口（/user/login）。
- 创建了ProductController，包含存在SQL注入漏洞的商品搜索接口（/product/search）。
- 创建了OrderController，包含存在SQL注入漏洞的订单查询接口（/order/list）。
- 所有接口均通过拼接SQL字符串，便于靶场演示SQL注入攻击。

### 2024-06-09（进度4）
- 数据库已由H2切换为MySQL。
- 依赖已更换为mysql-connector-j。
- application.yml已配置MySQL连接，默认用户名root，密码123456，数据库名vulnshop。
- 请确保本地已安装MySQL并创建vulnshop数据库。

### 2024-06-09（进度5）
- 新增Thymeleaf页面：login.html、products.html、product_detail.html、orders.html、order_detail.html。
- 控制器已支持页面渲染，所有注入点均有页面提示。
- 支持场景：登录、商品搜索、商品详情、订单查询、订单详情。
- 体验更贴近真实商场，便于SQL注入演练。

接下来将开发实体类、JPA仓库、控制器和前端页面，逐步实现购物平台的SQL注入靶场功能。 
