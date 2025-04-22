# Swift Code Management API

A Spring Boot application for managing SWIFT codes by country. It provides RESTful endpoints for querying, creating, and deleting SWIFT codes, as well as built-in API documentation and a persistent local H2 database.

---

## 📌 Project Assumptions

This project is developed under the following assumptions and design choices:

- **Excel Data Integrity:**  
  It is assumed that all data provided in the Excel file (`data.xlsx`) is **correct and valid** — no additional validation or sanitization is performed on input rows during import.

- **Irrelevant Columns Ignored:**  
  Columns such as `code type`, `town name`, and `timezone` are **intentionally ignored**, as they are not required for this application’s functionality.

- **Country Dependency for SWIFT Codes:**  
  When adding a new SWIFT code via API, it **must be linked to an existing country** in the database.  
  If the provided country does not exist, the API responds with an appropriate error message.

- **SWIFT Code Validation Rules:**  
  A valid SWIFT code must:
    - have a length of exactly **11 characters**
    - consist only of **uppercase letters and digits**

- **ISO2 Code Validation Rules:**  
  A valid ISO2 code must:
    - have a length of exactly **2 characters**
    - consist only of **uppercase letters**

---

## 📦 Tech Stack

- Java **21**
- Maven **4.0.0**
- **H2** Database (File-based, persistent)
- Lombok **1.18.36**
- Spring Boot **3.4.4**

---

## 🚀 Running the Application

> **IMPORTANT!!!:** To use Lombok, make sure to **enable annotation processing** in your IDE:
> - IntelliJ IDEA:  
    >   *Preferences → Build, Execution, Deployment → Compiler → Annotation Processors → Enable*
> - Eclipse:  
    >   *Preferences → Java → Compiler → Annotation Processing → Enable*

1. **Clone the repository:**

```bash
git clone https://github.com/Mikosztyla/swift_code_management_api.git
cd swiftcode
```

2. **Run the application:**

Using Maven wrapper:

```bash
./mvnw spring-boot:run
```

Or using Maven console:

```bash
mvn clean install
mvn spring-boot:run
```

---

## 🧪 Running Tests

To run all unit and integration tests:

```bash
mvn test
```

> Tests use a separate persistent H2 database at `data/test/testdb.mv.db`.

---

## 🔗 H2 Database

The application uses a file-based **persistent H2** database stored at `data/db/swiftcode.mv.db`.

> Access the database UI at:  
> 📍 [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

**Credentials:**

- **JDBC URL:** `jdbc:h2:file:./data/db/swiftcode`
- **Username:** `sa`
- **Password:** *(leave blank)*

## Database Schema

The application uses a relational database schema with two main entities: `Country` and `Swift`, connected via a one-to-many relationship.

### Tables

#### `countries`
Stores basic country information.

| Column   | Type    | Description                      |
|----------|---------|----------------------------------|
| `id`     | BIGINT  | Primary key (auto-generated)     |
| `name`   | VARCHAR | Country name                     |
| `iso2`   | CHAR(2) | ISO2 code                        |
| `swifts` | —       | One-to-many relation to `swifts` |

#### `swifts`
Stores SWIFT code data.

| Column            | Type    | Description                        |
|-------------------|---------|------------------------------------|
| `id`              | BIGINT  | Primary key (auto-generated)       |
| `swift_code`      | VARCHAR | Full SWIFT code (unique, required) |
| `base_swift_code` | VARCHAR | First 8 characters of SWIFT code   |
| `address`         | VARCHAR | Branch address                     |
| `bank_name`       | VARCHAR | Name of the bank                   |
| `is_headquarter`  | BOOLEAN | Flag indicating if it's the HQ     |
| `country_id`      | BIGINT  | Foreign key → `countries.id`       |

> A `Country` can have multiple `Swift` entries (banks), but each `Swift` is linked to exactly one `Country`.

> The `baseSwiftCode` is automatically extracted from the full `swiftCode` as its first 8 characters.

> The rest of the columns from the `data.xlsx` are irrelevant, therefore they are not in the database

---

## 📚 API Documentation

Interactive documentation is available via Swagger/OpenAPI:

- Swagger UI:  
  📍 [http://localhost:8080/swagger](http://localhost:8080/swagger)
- Raw OpenAPI JSON:  
  📍 [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

---

## 🧩 Project Structure

```
data
├── db
│   └── swiftcode.mv.db              # Main Database
├── test
│   └── testdb.mv.db                 # Database for testing
src
├── main
│   ├── java/remitly/task/swiftcode
│   │   ├── controller               # REST endpoints (see SwiftCodeController)
│   │   ├── dto                      # Data transfer objects
│   │   ├── model                    # Entity classes
│   │   ├── repository               # JPA repositories
│   │   ├── service                  # Business logic
│   │   └── DataInitializer.java     # Loads data.xlsx if DB is empty
│   └── resources
│       ├── application.properties          
│       ├── application-test.properties     # Test-specific config
│       └── data.xlsx                       # Excel seed file
├── test
│   └── java/remitly/task/swiftcode         # Unit and integration tests
```

---

## Notes

- All API endpoints are defined in `SwiftCodeController`.
- Initial data is loaded from `data.xlsx` — only if the database is empty.
- The main database is persistent and saved to `data/db/swiftcode.mv.db`.
- Tests use a separate persistent H2 DB at `data/test/testdb.mv.db`.

---
