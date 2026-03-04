# Spring Boot Demo

API REST demo xây dựng với Spring Boot 3, xác thực JWT, Spring Security và quản lý user với MySQL.

## Công nghệ sử dụng

| Thành phần        | Công nghệ                          |
|-------------------|-------------------------------------|
| Framework         | Spring Boot 3.5.x                  |
| Java              | 17                                 |
| Database          | MySQL                              |
| ORM               | Spring Data JPA / Hibernate        |
| Migration         | Flyway                             |
| Bảo mật           | Spring Security + JWT (jjwt 0.11.5) |
| Validation        | Bean Validation                    |
| Tiện ích          | Lombok, DevTools                   |

## Yêu cầu hệ thống

- **JDK 17**
- **Maven 3.6+**
- **MySQL** (hoặc XAMPP với MySQL) — port mặc định `3306`

## Cấu trúc dự án

```
src/main/java/com/example/springboot_demo/
├── config/              # Cấu hình (JWT, Security)
├── cronjob/             # Nhiệm vụ định kỳ (dọn token hết hạn, blacklist)
├── databases/seeder/    # Seed dữ liệu
├── helpers/             # Filter JWT, GlobalExceptionHandler
├── modules/users/
│   ├── controllers/     # AuthController, UserController
│   ├── entities/        # User, RefreshToken, BlacklistedToken
│   ├── reponsitories/   # Repository JPA
│   ├── request/         # DTO request (Login, RefreshToken, BlackListToken)
│   ├── resources/       # DTO response (Login, User, RefreshToken)
│   └── service/         # UserService, BlacklistService, CustomUserDetailsService
├── resources/           # ApiResource, MessageResource, ErrorResource
├── services/            # JwtService
└── DemoApplication.java
```

## Cấu hình

### 1. Cơ sở dữ liệu

Tạo database MySQL (ví dụ tên `spring`), sau đó chỉnh `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spring?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=<username>
spring.datasource.password=<password>
```

**Lưu ý:** Không commit mật khẩu thật lên Git; nên dùng biến môi trường hoặc profile riêng.

### 2. JWT

Các thuộc tính trong `application.properties`:

- `jwt.secret` — Secret key dùng ký/verify token
- `jwt.expiration` — Thời gian hết hạn access token (giây)
- `jwt.expirationRefreshToken` — Thời gian hết hạn refresh token (giây)
- `jwt.issuer` — Issuer của JWT

### 3. Cổng chạy ứng dụng

Mặc định server chạy tại **port 3001** (`server.port=3001`).

## Chạy ứng dụng

### Build và chạy bằng Maven

```bash
# Build
mvn clean install

# Chạy
mvn spring-boot:run
```

Ứng dụng sẽ chạy tại: **http://localhost:3001**

### Chạy từ IDE

Chạy class `com.example.springboot_demo.DemoApplication`.

## API Endpoints

### Base URL: `http://localhost:3001`

### Auth (`/v1/auth`) — không cần JWT

| Method | Endpoint              | Mô tả                          |
|--------|------------------------|---------------------------------|
| POST   | `/v1/auth/login`       | Đăng nhập, trả về access + refresh token |
| POST   | `/v1/auth/blacklisted_tokens` | Đưa token vào blacklist (logout) |
| GET    | `/v1/auth/logout`      | Logout (gửi Bearer token trong header)   |
| POST   | `/v1/auth/refresh`     | Làm mới access token bằng refresh token  |

### User — cần JWT (Bearer token)

| Method | Endpoint   | Mô tả                |
|--------|------------|------------------------|
| GET    | `/v1/me`   | Lấy thông tin user hiện tại |

### Khác

| Method | Endpoint | Mô tả        |
|--------|----------|--------------|
| GET    | `/health`| Health check (cho phép không cần auth) |

### Ví dụ request

**Login:**

```http
POST /v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password"
}
```

**Refresh token:**

```http
POST /v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "<refresh_token_string>"
}
```

**Lấy thông tin user (cần JWT):**

```http
GET /v1/me
Authorization: Bearer <access_token>
```

## Database & Flyway

Migration nằm trong `src/main/resources/db/migrations/`:

- `V2025_001__create_user_catalouges_table.sql`
- `V2025_002__create_users_table.sql`
- `V20205_003__Add_age_to_users_table.sql`
- `V20205_004__Create_blacklisted_tokens_table.sql`
- `V20205_005__Create_refresh_tokens_table.sql`

Khi khởi động, Flyway tự chạy các migration chưa áp dụng (đã bật `spring.flyway.enabled=true` và `spring.flyway.baseline-on-migrate=true`).

## Cron jobs

- **RefreshTokenClean** — Xóa refresh token hết hạn.
- **BlacklistTokenClean** — Dọn bớt bản ghi blacklisted token cũ.

Cần bật scheduling (`@EnableScheduling` đã có trong `DemoApplication`).

## License

Demo project — tùy dự án có thể thêm license phù hợp.
