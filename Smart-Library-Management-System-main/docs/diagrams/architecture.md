# System Architecture Diagram

Layered architecture: the console UI never talks to storage directly — every
request flows down through the service layer (business rules) and the DAO
layer (persistence), and every failure flows back up as a typed exception.

![Architecture Diagram](../images/architecture.png)

```mermaid
graph TB
    UI["Presentation Layer<br/>ConsoleUI (menu-driven)"]
    SVC["Service Layer<br/>UserService, BookService,<br/>TransactionService, ReportService,<br/>UserFactory, PasswordUtil"]
    DAO["Data Access Layer (DAO)<br/>BookDAO, UserDAO, TransactionDAO<br/>(CSV file-backed)"]
    DATA[("Persistence<br/>data/books.csv<br/>data/users.csv<br/>data/transactions.csv")]
    MODEL["Domain Model<br/>User (Admin/Librarian/Member)<br/>Book, Transaction"]
    UTIL["Cross-cutting Utilities<br/>AppLogger (Singleton)<br/>FileManager (Singleton)<br/>ValidationUtil, IdGenerator"]
    EXC["Custom Exceptions<br/>BookNotFoundException, UserNotFoundException,<br/>InvalidInputException, DuplicateEntryException,<br/>AuthenticationException, BookUnavailableException"]

    UI -->|calls| SVC
    SVC -->|calls| DAO
    DAO -->|reads/writes| DATA
    SVC -->|uses| MODEL
    DAO -->|builds/serializes| MODEL
    SVC -.->|throws| EXC
    UI -.->|catches| EXC
    SVC -.->|uses| UTIL
    DAO -.->|uses| UTIL
```
