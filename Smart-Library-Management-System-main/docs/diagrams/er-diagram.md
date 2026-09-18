# ER / Storage Design Diagram

The system persists three CSV-backed "tables". `TRANSACTIONS` is the
associative entity linking `USERS` and `BOOKS` (many issue/return records
per user, and per book).

![ER Diagram](../images/erdiagram.png)

```mermaid
erDiagram
    USERS ||--o{ TRANSACTIONS : "borrows"
    BOOKS ||--o{ TRANSACTIONS : "is borrowed in"

    USERS {
        string userId PK
        string name
        string username
        string passwordHash
        string role
        double outstandingFine
    }
    BOOKS {
        string bookId PK
        string title
        string author
        string isbn
        string category
        int totalCopies
        int availableCopies
    }
    TRANSACTIONS {
        string transactionId PK
        string bookId FK
        string userId FK
        date issueDate
        date dueDate
        date returnDate
        double fineAmount
        string status
    }
```
