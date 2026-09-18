package com.slms.ui;

import com.slms.exception.*;
import com.slms.model.*;
import com.slms.service.*;
import com.slms.util.AppLogger;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Menu-driven console front-end. Presents a different menu depending on the
 * logged-in user's role (role-based access control), and delegates all
 * actual work to the service layer.
 */
public class ConsoleUI {

    private final Scanner sc = new Scanner(System.in);
    private final UserService userService = new UserService();
    private final BookService bookService = new BookService();
    private final TransactionService transactionService = new TransactionService();
    private final ReportService reportService = new ReportService(bookService, transactionService, userService);
    private final AppLogger logger = AppLogger.getInstance();

    public void start() {
        seedAdminIfNeeded();
        System.out.println("=======================================");
        System.out.println(" Smart Library Management System (SLMS)");
        System.out.println("=======================================");
        boolean running = true;
        while (running) {
            System.out.println("\n1. Login\n2. Register as Member\n3. Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": loginFlow(); break;
                case "2": registerFlow(); break;
                case "3": running = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
        System.out.println("Goodbye!");
    }

    private void seedAdminIfNeeded() {
        if (userService.listAll().stream().noneMatch(u -> u.getRole() == Role.ADMIN)) {
            try {
                userService.register("System Admin", "admin", "admin123", Role.ADMIN);
                logger.info("Seeded default admin account (username: admin / password: admin123)");
                System.out.println("[First run] Created default admin -> username: admin, password: admin123");
            } catch (Exception e) {
                logger.error("Failed to seed admin account", e);
            }
        }
    }

    private void registerFlow() {
        try {
            System.out.print("Full name: ");
            String name = sc.nextLine();
            System.out.print("Choose a username: ");
            String username = sc.nextLine();
            System.out.print("Choose a password: ");
            String password = sc.nextLine();
            User created = userService.register(name, username, password, Role.MEMBER);
            System.out.println("Registered successfully. Your member id is " + created.getUserId());
        } catch (InvalidInputException | DuplicateEntryException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void loginFlow() {
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        try {
            User user = userService.login(username, password);
            System.out.println("Welcome, " + user.getName() + " (" + user.getDashboardTitle() + ")");
            dispatchMenu(user);
        } catch (AuthenticationException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void dispatchMenu(User user) {
        switch (user.getRole()) {
            case ADMIN: adminMenu(user); break;
            case LIBRARIAN: librarianMenu(user); break;
            case MEMBER: memberMenu((Member) user); break;
        }
    }

    // ---------------------------------------------------------------
    // Admin menu: full access
    // ---------------------------------------------------------------
    private void adminMenu(User admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- Admin Menu (" + admin.getName() + ") ---");
            System.out.println("1. Manage Catalog\n2. Manage Transactions\n3. Manage Users\n4. Reports\n5. Logout");
            switch (sc.nextLine().trim()) {
                case "1": catalogMenu(); break;
                case "2": transactionMenu(admin); break;
                case "3": userManagementMenu(); break;
                case "4": reportsMenu(); break;
                case "5": loggedIn = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // ---------------------------------------------------------------
    // Librarian menu: catalog + transactions
    // ---------------------------------------------------------------
    private void librarianMenu(User librarian) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- Librarian Menu (" + librarian.getName() + ") ---");
            System.out.println("1. Manage Catalog\n2. Manage Transactions\n3. Operational Reports\n4. Logout");
            switch (sc.nextLine().trim()) {
                case "1": catalogMenu(); break;
                case "2": transactionMenu(librarian); break;
                case "3":
                    System.out.println(reportService.inventorySummary());
                    printOverdue();
                    break;
                case "4": loggedIn = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // ---------------------------------------------------------------
    // Member menu: browse + borrow/return + own history
    // ---------------------------------------------------------------
    private void memberMenu(Member member) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- Member Menu (" + member.getName() + ") ---");
            System.out.println("1. Browse/Search Catalog\n2. Borrow a Book\n3. Return a Book"
                    + "\n4. My Borrowing History\n5. Logout");
            switch (sc.nextLine().trim()) {
                case "1": searchBooksFlow(); break;
                case "2": borrowFlow(member); break;
                case "3": returnFlow(); break;
                case "4": myHistoryFlow(member); break;
                case "5": loggedIn = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // ---------------------------------------------------------------
    // Catalog management (shared by Admin/Librarian)
    // ---------------------------------------------------------------
    private void catalogMenu() {
        System.out.println("\n-- Catalog Management --");
        System.out.println("1. Add Book\n2. Update Book\n3. Remove Book\n4. List All Books\n5. Search Books\n6. Back");
        switch (sc.nextLine().trim()) {
            case "1": addBookFlow(); break;
            case "2": updateBookFlow(); break;
            case "3": removeBookFlow(); break;
            case "4": listAllBooks(); break;
            case "5": searchBooksFlow(); break;
            case "6": break;
            default: System.out.println("Invalid choice.");
        }
    }

    private void addBookFlow() {
        try {
            System.out.print("Title: ");
            String title = sc.nextLine();
            System.out.print("Author: ");
            String author = sc.nextLine();
            System.out.print("ISBN (10 or 13 digits): ");
            String isbn = sc.nextLine();
            System.out.print("Category: ");
            String category = sc.nextLine();
            System.out.print("Total copies: ");
            int copies = Integer.parseInt(sc.nextLine().trim());
            Book book = bookService.addBook(title, author, isbn, category, copies);
            System.out.println("Added: " + book);
        } catch (InvalidInputException | DuplicateEntryException | NumberFormatException e) {
            System.out.println("Could not add book: " + e.getMessage());
        }
    }

    private void updateBookFlow() {
        try {
            System.out.print("Book id to update: ");
            String id = sc.nextLine().trim();
            System.out.print("New title: ");
            String title = sc.nextLine();
            System.out.print("New author: ");
            String author = sc.nextLine();
            System.out.print("New category: ");
            String category = sc.nextLine();
            System.out.print("New total copies: ");
            int copies = Integer.parseInt(sc.nextLine().trim());
            bookService.updateBook(id, title, author, category, copies);
            System.out.println("Book updated.");
        } catch (BookNotFoundException | InvalidInputException | NumberFormatException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    private void removeBookFlow() {
        try {
            System.out.print("Book id to remove: ");
            String id = sc.nextLine().trim();
            bookService.removeBook(id);
            System.out.println("Book removed.");
        } catch (BookNotFoundException e) {
            System.out.println("Remove failed: " + e.getMessage());
        }
    }

    private void listAllBooks() {
        List<Book> books = bookService.listAll();
        if (books.isEmpty()) { System.out.println("Catalog is empty."); return; }
        books.forEach(System.out::println);
    }

    private void searchBooksFlow() {
        System.out.print("Search keyword (title/author/category): ");
        String keyword = sc.nextLine();
        List<Book> results = bookService.search(keyword);
        if (results.isEmpty()) { System.out.println("No matches."); return; }
        results.forEach(System.out::println);
    }

    // ---------------------------------------------------------------
    // Transaction management
    // ---------------------------------------------------------------
    private void transactionMenu(User actingUser) {
        System.out.println("\n-- Transaction Management --");
        System.out.println("1. Issue Book to Member\n2. Return Book\n3. List All Transactions\n4. Overdue Report\n5. Back");
        switch (sc.nextLine().trim()) {
            case "1": issueForMemberFlow(); break;
            case "2": returnFlow(); break;
            case "3": transactionService.listAll().forEach(System.out::println); break;
            case "4": printOverdue(); break;
            case "5": break;
            default: System.out.println("Invalid choice.");
        }
    }

    private void issueForMemberFlow() {
        try {
            System.out.print("Member user id: ");
            String userId = sc.nextLine().trim();
            System.out.print("Book id: ");
            String bookId = sc.nextLine().trim();
            Transaction t = transactionService.issueBook(userId, bookId);
            System.out.println("Issued. " + t);
        } catch (BookNotFoundException | BookUnavailableException e) {
            System.out.println("Issue failed: " + e.getMessage());
        }
    }

    private void borrowFlow(Member member) {
        try {
            System.out.print("Book id to borrow: ");
            String bookId = sc.nextLine().trim();
            Transaction t = transactionService.issueBook(member.getUserId(), bookId);
            System.out.println("Borrowed successfully. Due date: " + t.getDueDate());
        } catch (BookNotFoundException | BookUnavailableException e) {
            System.out.println("Borrow failed: " + e.getMessage());
        }
    }

    private void returnFlow() {
        try {
            System.out.print("Transaction id to return: ");
            String txnId = sc.nextLine().trim();
            Transaction t = transactionService.returnBook(txnId);
            System.out.println("Returned. Fine due: " + t.getFineAmount());
        } catch (InvalidInputException | BookNotFoundException e) {
            System.out.println("Return failed: " + e.getMessage());
        }
    }

    private void myHistoryFlow(Member member) {
        List<Transaction> history = transactionService.historyForUser(member.getUserId());
        if (history.isEmpty()) { System.out.println("No borrowing history yet."); return; }
        history.forEach(System.out::println);
    }

    // ---------------------------------------------------------------
    // User management (Admin only)
    // ---------------------------------------------------------------
    private void userManagementMenu() {
        System.out.println("\n-- User Management --");
        System.out.println("1. List Users\n2. Register Staff (Librarian/Admin)\n3. Delete User\n4. Back");
        switch (sc.nextLine().trim()) {
            case "1": userService.listAll().forEach(u ->
                    System.out.println(u.getUserId() + " | " + u.getName() + " | " + u.getUsername() + " | " + u.getRole()));
                break;
            case "2": registerStaffFlow(); break;
            case "3": deleteUserFlow(); break;
            case "4": break;
            default: System.out.println("Invalid choice.");
        }
    }

    private void registerStaffFlow() {
        try {
            System.out.print("Full name: ");
            String name = sc.nextLine();
            System.out.print("Username: ");
            String username = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();
            System.out.print("Role (ADMIN/LIBRARIAN): ");
            Role role = Role.valueOf(sc.nextLine().trim().toUpperCase());
            User created = userService.register(name, username, password, role);
            System.out.println("Created " + role + " with id " + created.getUserId());
        } catch (InvalidInputException | DuplicateEntryException | IllegalArgumentException e) {
            System.out.println("Could not register staff: " + e.getMessage());
        }
    }

    private void deleteUserFlow() {
        try {
            System.out.print("User id to delete: ");
            String id = sc.nextLine().trim();
            userService.deleteUser(id);
            System.out.println("User deleted.");
        } catch (UserNotFoundException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Reports (Admin only, deeper set)
    // ---------------------------------------------------------------
    private void reportsMenu() {
        System.out.println("\n-- Reports & Analytics --");
        System.out.println("1. Inventory Summary\n2. Most Borrowed Books\n3. Overdue Report\n4. Member Activity\n5. Back");
        switch (sc.nextLine().trim()) {
            case "1": System.out.println(reportService.inventorySummary()); break;
            case "2":
                for (Map.Entry<Book, Long> e : reportService.mostBorrowedBooks(5)) {
                    System.out.println(e.getValue() + "x  " + e.getKey());
                }
                break;
            case "3": printOverdue(); break;
            case "4":
                System.out.print("Member user id: ");
                String uid = sc.nextLine().trim();
                System.out.println(reportService.memberActivityReport(uid));
                break;
            case "5": break;
            default: System.out.println("Invalid choice.");
        }
    }

    private void printOverdue() {
        List<Transaction> overdue = reportService.overdueReport();
        if (overdue.isEmpty()) { System.out.println("No overdue items. Great job!"); return; }
        overdue.forEach(System.out::println);
    }
}
