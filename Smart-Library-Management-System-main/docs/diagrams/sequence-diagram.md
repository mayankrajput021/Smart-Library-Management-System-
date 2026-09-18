# Sequence Diagram — Issue Book

Shows the message flow when a Librarian (or Member, self-service) issues a
book: the `TransactionService` checks availability, records the transaction,
and updates the book's available copy count.

![Sequence Diagram](../images/sequence_issue.png)

```mermaid
sequenceDiagram
    actor U as Member/Librarian
    participant UI as ConsoleUI
    participant TS as TransactionService
    participant BD as BookDAO
    participant TD as TransactionDAO

    U->>UI: selects "Borrow / Issue Book"
    UI->>TS: issueBook(userId, bookId)
    TS->>BD: findById(bookId)
    BD-->>TS: Book
    TS->>TD: findActiveByUserAndBook(userId, bookId)
    TD-->>TS: [] (none active)
    TS->>TD: add(new Transaction)
    TD-->>TS: transactionId
    TS->>BD: update(book with decremented availability)
    TS-->>UI: Transaction (ISSUED)
    UI-->>U: shows due date
```
