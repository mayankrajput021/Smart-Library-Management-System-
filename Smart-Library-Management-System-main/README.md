# Smart Library Management System (SLMS)

This project is a console-based Student Management System developed in Core Java for the “Build Your Own Project” submission of the Java Programming course. It combines fundamental and advanced Java concepts, including abstraction, inheritance, polymorphism, interfaces, collections, exception handling, file handling, and design patterns, to build a structured application for managing student information and academic records.

## Overview

SMS allows an Admin, Faculty, or Student to log in through a role-based menu to manage student records, maintain course information, record attendance and marks, and generate academic reports. Data is stored in human-readable CSV files, allowing the complete application to run using only Core Java and a JDK, without requiring any external libraries or dependencies.

## Features

## Features

**Role-Based Authentication** — Separate login access is provided for Admin, Faculty, and Student users. Each role is directed to a dedicated menu with permissions appropriate to their responsibilities.

**Student Records** — Manage complete student profiles with options to add, edit, delete, search, and display records using student ID, name, department, or semester.

**Course Management** — Administrators and faculty can maintain course information, while students can view available subjects and register for their required courses.

**Attendance Management** — Faculty can record and update subject-wise attendance, while students can view their attendance percentage and attendance history.

**Marks & Grade Processing** — Faculty can enter examination marks, after which the system calculates total marks, percentage, and grades automatically.

**User Administration** — Students can create their own accounts, while administrators can add faculty members, modify user details, and remove accounts when required.

**Academic Reports** — Generate student performance summaries, attendance reports, course-wise statistics, and individual result reports for easier academic analysis.

**Validation & Exception Handling** — Invalid inputs and unavailable records are handled using custom exceptions such as `InvalidInputException`, `DuplicateEntryException`, `StudentNotFoundException`, `CourseNotFoundException`, and `AuthenticationException`.

**CSV-Based Storage** — Student, course, attendance, marks, and user information is stored in human-readable CSV files, providing persistent storage without requiring a database.

**Application Logging** — Important events such as user login, record updates, attendance entries, result modifications, and errors are recorded with timestamps in `logs/app.log` using a Singleton-based logger.

**Password Security** — User passwords are converted into SHA-256 hashes before being stored, ensuring that plain-text credentials are never persisted.

**Testing Support** — Core application functionality is verified through independent test cases covering authentication, validation, registration, enrollment, attendance, marks, and exception-handling scenarios.
## Technologies / Tools Used

| Concern            | Choice                                            |
|--------------------|----------------------------------------------------|
| Language           | Java 17+ (developed & tested on OpenJDK 21)        |
| Persistence        | Plain CSV files under `data/` (no DB engine needed)|
| Build              | `javac`/`java` directly, or the provided `build.sh`|
| Testing            | Hand-rolled `Assert` harness + in-memory fake DAOs |
| Design patterns    | Singleton, Factory, DAO                            |
| Diagrams           | Mermaid (renders natively on GitHub) + Graphviz PNGs|

No external libraries, JDBC drivers, or Maven/Gradle setup are required —
this keeps the project trivial to clone and run anywhere a JDK is
installed.

## Project Structure

```
LibraryManagementSystem/
├── README.md
├── statement.md
├── build.sh                     # compile + run helper script
├── .gitignore
├── docs/
│   ├── diagrams/                 # Mermaid diagrams (render on GitHub) + PNG links
│   │   ├── architecture.md
│   │   ├── usecase.md
│   │   ├── class-diagram.md
│   │   ├── sequence-diagram.md
│   │   ├── er-diagram.md
│   │   └── workflow.md
│   ├── images/                   # Rendered PNGs used by the diagrams and the PDF report
│   └── ProjectReport.pdf         # Full design & evaluation report
├── src/
│   ├── main/java/com/slms/
│   │   ├── Main.java
│   │   ├── model/                # Role, User, Admin, Librarian, Member, Book, Transaction, TransactionStatus
│   │   ├── exception/             # 6 custom checked exceptions
│   │   ├── util/                  # AppLogger, FileManager (Singletons), ValidationUtil, IdGenerator
│   │   ├── dao/                   # BookDAO/UserDAO/TransactionDAO + CSV-backed impls
│   │   ├── service/                # UserService, BookService, TransactionService, ReportService, UserFactory, PasswordUtil
│   │   └── ui/                     # ConsoleUI (menu-driven front end)
│   └── test/java/com/slms/test/    # TestRunner + service tests + in-memory fake DAOs
├── data/                          # Generated at runtime (books.csv, users.csv, transactions.csv)
└── logs/                          # Generated at runtime (app.log)
```

## Design Diagrams

All diagrams live in [`docs/diagrams/`](docs/diagrams) as Mermaid code
(renders automatically on GitHub) with a matching PNG in
[`docs/images/`](docs/images):

- [System Architecture](docs/diagrams/architecture.md)
- [Use Case Diagram](docs/diagrams/usecase.md)
- [Class / Component Diagram](docs/diagrams/class-diagram.md)
- [Sequence Diagram — Issue Book](docs/diagrams/sequence-diagram.md)
- [ER / Storage Design](docs/diagrams/er-diagram.md)
- [Process Flow / Workflow](docs/diagrams/workflow.md)

## Non-Functional Requirements

| Requirement       | How it's addressed                                                                 |
|--------------------|-------------------------------------------------------------------------------------|
| **Performance**    | In-memory list operations over small CSV datasets; O(n) lookups are acceptable at the target scale (a single library branch) and isolated behind the DAO interfaces so storage can later be swapped for a database with no service-layer changes. |
| **Security**       | Passwords are SHA-256 hashed before storage; role-based menus prevent Members from reaching Admin/Librarian operations. |
| **Reliability**    | Every service method validates input and fails predictably via typed exceptions instead of crashing; file writes are atomic per-operation (read-modify-write-all). |
| **Scalability**    | Layered architecture (UI → Service → DAO → Storage) means the CSV storage engine can be replaced by JDBC/a real database by only rewriting the DAO implementations. |
| **Maintainability**| Clear package-by-layer structure, Javadoc-style comments, and consistent naming make the codebase easy to extend (e.g. adding a new report or role). |
| **Error handling** | Six custom checked exceptions communicate exactly what went wrong; the UI layer catches and reports each one with a human-readable message instead of a stack trace. |
| **Logging/monitoring** | A Singleton `AppLogger` timestamps every significant action and error to `logs/app.log`. |
| **Resource efficiency** | Files are opened, read/written, and closed per operation (try-with-resources); no persistent open handles or connection pools are needed for this scale. |

## Installation & Running

**Prerequisites:** JDK 17 or later (`javac`/`java` on your `PATH`).

```bash
# 1. Clone the repository
git clone https://github.com/mayankrajput021/Smart-Library-Management-System-.git
cd LibraryManagementSystem

# 2. Compile the main application
javac -d out $(find src/main -name "*.java")

# 3. Run it
java -cp out com.slms.Main
```

Or simply run the provided helper script:

```bash
chmod +x build.sh
./build.sh run
```

On first run, SLMS automatically creates a default Admin account:

```
username: admin
password: admin123
```

(Change this password immediately after first login in a real deployment —
`UserService` has no built-in password-change flow yet; see
**Future Enhancements** in the report.)

## Testing

The project ships with a dependency-free unit test suite (no JUnit/Maven
required — the build has no internet access to Maven Central, so tests run
against small in-memory fake DAOs instead):

```bash
# Compile main + test sources
javac -d out $(find src/main -name "*.java")
javac -d testout -cp out $(find src/test -name "*.java")

# Run all tests
java -cp out:testout com.slms.test.TestRunner
```

Or: `./build.sh test`

Expected output ends with a summary line such as:

```
Passed: 20  Failed: 0
```

Tests cover: input validation (empty fields, invalid ISBNs, non-positive
counts), duplicate detection (usernames, ISBNs), authentication
(correct/incorrect credentials), password hashing, book issue/return
lifecycle, availability tracking, and fine calculation.

## Screenshots

Run `./build.sh run` and try the flow: **Login as admin → Manage Catalog →
Add Book → Manage Transactions → Issue Book → Reports → Inventory
Summary** to see the full module set in action. (Add your own terminal
screenshots here before submission if required by your instructor.)

## License

Academic project submitted for course evaluation. No specific license
applied; reuse for educational purposes.

## Author
Mayank (24BAC10071)
