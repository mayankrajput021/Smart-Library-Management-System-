# Class / Component Diagram

Highlights the OOP design: an abstract `User` with three concrete role
subclasses, the domain entities `Book` and `Transaction`, the service layer
that orchestrates them, and the `UserFactory` (Factory pattern) used to
instantiate the correct `User` subtype.

![Class Diagram](../images/classdiagram.png)

```mermaid
classDiagram
    class User {
        <<abstract>>
        -String userId
        -String name
        -String username
        -String passwordHash
        -Role role
        +getDashboardTitle() String
        +getPermissionSummary() String
    }
    class Admin
    class Librarian
    class Member {
        -double outstandingFine
        +addFine(amount)
        +clearFine()
    }
    User <|-- Admin
    User <|-- Librarian
    User <|-- Member

    class Book {
        -String bookId
        -String title
        -String author
        -String isbn
        -String category
        -int totalCopies
        -int availableCopies
        +isAvailable() boolean
        +decrementAvailable()
        +incrementAvailable()
    }

    class Transaction {
        -String transactionId
        -String bookId
        -String userId
        -LocalDate issueDate
        -LocalDate dueDate
        -LocalDate returnDate
        -double fineAmount
        -TransactionStatus status
    }

    class UserFactory {
        <<factory>>
        +createUser(role, ...) User
    }
    class UserService { +register() +login() +deleteUser() }
    class BookService { +addBook() +updateBook() +removeBook() +search() }
    class TransactionService { +issueBook() +returnBook() +refreshOverdueStatuses() }
    class ReportService { +mostBorrowedBooks() +overdueReport() +memberActivityReport() }

    UserService ..> User : manages
    UserService ..> UserFactory : uses
    BookService ..> Book : manages
    TransactionService ..> Transaction : manages
    TransactionService ..> Book : updates
    ReportService ..> BookService : uses
    ReportService ..> TransactionService : uses
    ReportService ..> UserService : uses
    Transaction --> Book : refers to
    Transaction --> User : refers to
```
