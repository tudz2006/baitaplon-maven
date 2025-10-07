# Baitaplon Maven - Ứng dụng Quản lý Bán hàng

## Mô tả
Đây là ứng dụng Java Swing để quản lý bán hàng với các chức năng chính:
- **Thống kê**: Xem báo cáo doanh thu, sản phẩm
- **Bán hàng**: Quản lý giao dịch bán hàng
- **Nhập hàng**: Quản lý nhập kho
- **Lịch sử**: Xem lịch sử giao dịch

## Công nghệ sử dụng
- **Java 17+**
- **Java Swing** - Giao diện người dùng desktop
- **MySQL** - Cơ sở dữ liệu
- **Maven** - Quản lý dự án và dependencies
- **Gson** - Xử lý JSON
- **SwingX** - Các component Swing nâng cao (AutoComplete, etc.)
- **SLF4J + Logback** - Logging
- **JUnit 5** - Unit testing
- **Apache Commons** - Tiện ích cho String, IO, Collections

## Yêu cầu hệ thống
- Java 17 hoặc cao hơn
- Maven 3.6+
- MySQL 8.0+
- IDE: Apache NetBeans (khuyến nghị)

## Cài đặt và chạy

### 1. Cài đặt MySQL
```sql
CREATE DATABASE baitaplonjava;
```

### 2. Clone và build dự án
```bash
git clone <repository-url>
cd baitaplon-maven
mvn clean compile
```

### 3. Chạy ứng dụng
```bash
# Chạy trực tiếp với Maven
mvn exec:java -Dexec.mainClass="baitaplon.baitaplon.BaitaplonMaven"

# Hoặc chạy script batch (Windows)
run.bat

# Hoặc tạo JAR file
mvn clean package
java -jar target/baitaplon-maven-1.0-SNAPSHOT.jar
```

## Cấu hình Database
Mặc định ứng dụng kết nối đến:
- Host: localhost
- Database: baitaplonjava
- Username: root
- Password: (trống)

Bạn có thể thay đổi cấu hình trong class `DB.java` hoặc tạo constructor với tham số.

## Dependencies đã thêm

### Core Dependencies
- **MySQL Connector/J 8.0.33** - Kết nối MySQL
- **Gson 2.10.1** - Xử lý JSON
- **SwingX 1.6.5-1** - Component Swing nâng cao

### Development Dependencies
- **JUnit 5.9.3** - Unit testing
- **SLF4J 2.0.7** - Logging API
- **Logback 1.4.8** - Logging implementation

### Utility Libraries
- **Apache Commons Lang3 3.12.0** - Tiện ích cho String, Date, etc.
- **Apache Commons IO 2.11.0** - Tiện ích cho File operations
- **Apache Commons Collections4 4.4** - Collection utilities

## Cấu trúc dự án
```
src/
├── main/
│   ├── java/
│   │   └── baitaplon/
│   │       ├── baitaplon/          # Main class
│   │       ├── backend/            # Business logic
│   │       ├── view/               # UI components
│   │       ├── DB.java             # Database layer
│   │       └── main_layout.java    # Main window
│   └── resources/
│       └── logback.xml             # Logging configuration
└── test/
    └── java/                       # Test files
```

## Logging
Ứng dụng sử dụng SLF4J + Logback để logging:
- Console output với format thời gian
- File logging vào `logs/baitaplon.log`
- Rotation hàng ngày, giữ tối đa 30 ngày
- Log level: INFO (có thể thay đổi trong logback.xml)

## Testing
```bash
mvn test
```

## Build và Deploy
```bash
# Tạo JAR file với tất cả dependencies
mvn clean package

# JAR file sẽ được tạo tại: target/baitaplon-maven-1.0-SNAPSHOT.jar
```

## Troubleshooting

### Lỗi kết nối MySQL
- Kiểm tra MySQL service đang chạy
- Kiểm tra username/password
- Kiểm tra database `baitaplonjava` đã được tạo

### Lỗi Swing UI
- Đảm bảo Java 17+ được sử dụng
- Kiểm tra display properties trên Linux/Mac

### Lỗi dependencies
```bash
mvn clean install -U
```

## Đóng góp
1. Fork repository
2. Tạo feature branch
3. Commit changes
4. Push to branch
5. Tạo Pull Request

## License
Dự án này được phát hành dưới giấy phép mặc định của NetBeans.

