package com.example;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.sql.Date;
import java.time.temporal.ChronoUnit;

class ConsoleColors {
    // Reset
    public static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    // Background
    public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
    public static final String RED_BACKGROUND = "\033[41m";    // RED
    public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
    public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
    public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE
}

class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/library_management";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "shreya@123"; // Your MySQL password here

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
}

class User {
    private int userId;
    private String username;
    private String password;
    private String role;
    private String fullName;
    private String email;
    private String phone;

    public User(int userId, String username, String password, String role, String fullName, String email, String phone) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}

class Book {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private int publishedYear;
    private int quantity;
    private int availableQuantity;

    public Book(int bookId, String title, String author, String isbn, String genre, 
                int publishedYear, int quantity, int availableQuantity) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.publishedYear = publishedYear;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
    }

    // Getters
    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getGenre() { return genre; }
    public int getPublishedYear() { return publishedYear; }
    public int getQuantity() { return quantity; }
    public int getAvailableQuantity() { return availableQuantity; }
}

class Transaction {
    private int transactionId;
    private int userId;
    private int bookId;
    private Date issueDate;
    private Date dueDate;
    private Date returnDate;
    private double fineAmount;
    private String status;
    private String bookTitle;
    private String userName;

    public Transaction(int transactionId, int userId, int bookId, Date issueDate, 
                      Date dueDate, Date returnDate, double fineAmount, String status,
                      String bookTitle, String userName) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.bookId = bookId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
        this.status = status;
        this.bookTitle = bookTitle;
        this.userName = userName;
    }

    // Getters
    public int getTransactionId() { return transactionId; }
    public int getUserId() { return userId; }
    public int getBookId() { return bookId; }
    public Date getIssueDate() { return issueDate; }
    public Date getDueDate() { return dueDate; }
    public Date getReturnDate() { return returnDate; }
    public double getFineAmount() { return fineAmount; }
    public String getStatus() { return status; }
    public String getBookTitle() { return bookTitle; }
    public String getUserName() { return userName; }
}

public class Libraray {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;
    private static final double FINE_PER_DAY = 5.0; // $5 fine per day for overdue books

    public static void main(String[] args) {
        try {
            // Test database connection
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                System.out.println(ConsoleColors.GREEN + "Database connection established successfully!" + ConsoleColors.RESET);
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "Failed to connect to database: " + e.getMessage() + ConsoleColors.RESET);
            System.out.println("Please make sure MySQL is running and the database is properly set up.");
            return;
        }

        displayWelcomeScreen();
        
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                if (currentUser.getRole().equals("admin")) {
                    showAdminMenu();
                } else {
                    showStudentMenu();
                }
            }
        }
    }
    
    private static void displayWelcomeScreen() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔════════════════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "           WELCOME TO LIBRARY MANAGEMENT SYSTEM           " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                   (JDBC Version)                        " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚════════════════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "\nPress Enter to continue..." + ConsoleColors.RESET);
        scanner.nextLine();
    }

    private static void showLoginMenu() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                   LOGIN MENU                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  1. Login                                     " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  2. Exit                                      " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter your choice (1-2): " + ConsoleColors.RESET);
        
        int choice = getIntInput(1, 2);
        
        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                System.out.println(ConsoleColors.GREEN + "\nThank you for using Library Management System. Goodbye!");
                System.out.println(ConsoleColors.CYAN + "Happy Reading! 📚" + ConsoleColors.RESET);
                System.exit(0);
                break;
        }
    }

    private static void login() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                   USER LOGIN                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter username: " + ConsoleColors.RESET);
        String username = scanner.nextLine();
        
        System.out.print(ConsoleColors.YELLOW + "Enter password: " + ConsoleColors.RESET);
        String password = scanner.nextLine();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                currentUser = new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone")
                );
                
                System.out.println(ConsoleColors.GREEN + "\n✓ Login successful! Welcome, " + currentUser.getFullName() + "!" + ConsoleColors.RESET);
                pressEnterToContinue();
            } else {
                System.out.println(ConsoleColors.RED + "\n✗ Invalid username or password." + ConsoleColors.RESET);
                pressEnterToContinue();
            }
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
            pressEnterToContinue();
        }
    }

    private static void showAdminMenu() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                 ADMIN MENU                    " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "            Welcome, " + 
                         String.format("%-25s", currentUser.getFullName()) + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  1. Manage Books                             " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  2. Manage Users                             " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  3. View All Transactions                    " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  4. View Payments                            " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  5. Generate Reports                         " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  6. Logout                                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter your choice (1-6): " + ConsoleColors.RESET);
        
        int choice = getIntInput(1, 6);
        
        switch (choice) {
            case 1:
                manageBooks();
                break;
            case 2:
                manageUsers();
                break;
            case 3:
                viewAllTransactions();
                break;
            case 4:
                viewPayments();
                break;
            case 5:
                generateReports();
                break;
            case 6:
                currentUser = null;
                System.out.println(ConsoleColors.GREEN + "Logging out..." + ConsoleColors.RESET);
                pressEnterToContinue();
                break;
        }
    }

    private static void showStudentMenu() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                STUDENT MENU                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "            Welcome, " + 
                         String.format("%-25s", currentUser.getFullName()) + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  1. Browse Books                             " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  2. Issue Book                               " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  3. Return Book                              " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  4. My Transactions                          " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  5. Pay Fines                               " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  6. Logout                                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter your choice (1-6): " + ConsoleColors.RESET);
        
        int choice = getIntInput(1, 6);
        
        switch (choice) {
            case 1:
                browseBooks();
                break;
            case 2:
                issueBook();
                break;
            case 3:
                returnBook();
                break;
            case 4:
                viewMyTransactions();
                break;
            case 5:
                payFines();
                break;
            case 6:
                currentUser = null;
                System.out.println(ConsoleColors.GREEN + "Logging out..." + ConsoleColors.RESET);
                pressEnterToContinue();
                break;
        }
    }

    private static void manageBooks() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                MANAGE BOOKS                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  1. Add New Book                            " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  2. View All Books                          " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  3. Update Book                             " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  4. Delete Book                             " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  5. Back to Admin Menu                      " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter your choice (1-5): " + ConsoleColors.RESET);
        
        int choice = getIntInput(1, 5);
        
        switch (choice) {
            case 1:
                addBook();
                break;
            case 2:
                viewAllBooks();
                break;
            case 3:
                updateBook();
                break;
            case 4:
                deleteBook();
                break;
            case 5:
                return;
        }
    }

    private static void addBook() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                 ADD NEW BOOK                  " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter Book Title: " + ConsoleColors.RESET);
        String title = scanner.nextLine();
        
        System.out.print(ConsoleColors.YELLOW + "Enter Author: " + ConsoleColors.RESET);
        String author = scanner.nextLine();
        
        System.out.print(ConsoleColors.YELLOW + "Enter ISBN: " + ConsoleColors.RESET);
        String isbn = scanner.nextLine();
        
        System.out.print(ConsoleColors.YELLOW + "Enter Genre: " + ConsoleColors.RESET);
        String genre = scanner.nextLine();
        
        System.out.print(ConsoleColors.YELLOW + "Enter Published Year: " + ConsoleColors.RESET);
        int year = getIntInput(1000, Calendar.getInstance().get(Calendar.YEAR));
        
        System.out.print(ConsoleColors.YELLOW + "Enter Quantity: " + ConsoleColors.RESET);
        int quantity = getIntInput(1, 100);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO books (title, author, isbn, genre, published_year, quantity, available_quantity) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, isbn);
            stmt.setString(4, genre);
            stmt.setInt(5, year);
            stmt.setInt(6, quantity);
            stmt.setInt(7, quantity);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println(ConsoleColors.GREEN + "\n✓ Book added successfully!" + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.RED + "\n✗ Failed to add book." + ConsoleColors.RESET);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println(ConsoleColors.RED + "\n✗ A book with this ISBN already exists." + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
            }
        }
        
        pressEnterToContinue();
        manageBooks();
    }

    private static void viewAllBooks() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                 ALL BOOKS                     " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM books ORDER BY title";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (!rs.isBeforeFirst()) {
                System.out.println(ConsoleColors.YELLOW + "\nNo books found in the database." + ConsoleColors.RESET);
                pressEnterToContinue();
                manageBooks();
                return;
            }
            
            System.out.println(ConsoleColors.CYAN + "\n╔══════════╦══════════════════════════════════════╦══════════════════════╦══════════╦═══════╗");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "ID" + ConsoleColors.CYAN + "       ║ " + ConsoleColors.WHITE_BOLD + "Title" + 
                             ConsoleColors.CYAN + "                           ║ " + ConsoleColors.WHITE_BOLD + "Author" + 
                             ConsoleColors.CYAN + "              ║ " + ConsoleColors.WHITE_BOLD + "Available" + ConsoleColors.CYAN + " ║ " + 
                             ConsoleColors.WHITE_BOLD + "Total" + ConsoleColors.CYAN + " ║");
            System.out.println("╠══════════╬══════════════════════════════════════╬══════════════════════╬══════════╬═══════╣");
            
            while (rs.next()) {
                System.out.println("║ " + ConsoleColors.YELLOW + String.format("%-8s", rs.getInt("book_id")) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + String.format("%-36s", 
                                 truncateString(rs.getString("title"), 36)) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + String.format("%-20s", 
                                 truncateString(rs.getString("author"), 20)) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + 
                                 String.format("%5s", rs.getInt("available_quantity")) + 
                                 ConsoleColors.CYAN + "   ║ " + ConsoleColors.WHITE + 
                                 String.format("%4s", rs.getInt("quantity")) + 
                                 ConsoleColors.CYAN + "  ║");
            }
            
            System.out.println("╚══════════╩══════════════════════════════════════╩══════════════════════╩══════════╩═══════╝" + ConsoleColors.RESET);
            
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
        manageBooks();
    }

    private static void updateBook() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                UPDATE BOOK                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter Book ID to update: " + ConsoleColors.RESET);
        int bookId = getIntInput(1, Integer.MAX_VALUE);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if book exists
            String checkSql = "SELECT * FROM books WHERE book_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println(ConsoleColors.RED + "\n✗ Book not found with ID: " + bookId + ConsoleColors.RESET);
                pressEnterToContinue();
                manageBooks();
                return;
            }
            
            System.out.println(ConsoleColors.CYAN + "\nCurrent Book Details:");
            System.out.println("Title: " + rs.getString("title"));
            System.out.println("Author: " + rs.getString("author"));
            System.out.println("Available: " + rs.getInt("available_quantity") + "/" + rs.getInt("quantity") + ConsoleColors.RESET);
            
            System.out.print(ConsoleColors.YELLOW + "\nEnter new Title (press Enter to keep current): " + ConsoleColors.RESET);
            String title = scanner.nextLine();
            
            System.out.print(ConsoleColors.YELLOW + "Enter new Author (press Enter to keep current): " + ConsoleColors.RESET);
            String author = scanner.nextLine();
            
            System.out.print(ConsoleColors.YELLOW + "Enter new Quantity (enter 0 to keep current): " + ConsoleColors.RESET);
            int quantityInput = getIntInput(0, Integer.MAX_VALUE);
            
            // Calculate available quantity adjustment
            int currentQuantity = rs.getInt("quantity");
            int currentAvailable = rs.getInt("available_quantity");
            int newAvailable = currentAvailable;
            
            if (quantityInput > 0) {
                if (quantityInput > currentQuantity) {
                    // Adding more copies
                    newAvailable += (quantityInput - currentQuantity);
                } else if (quantityInput < currentQuantity) {
                    // Removing copies, but can't remove more than available
                    int toRemove = currentQuantity - quantityInput;
                    if (toRemove > (currentQuantity - currentAvailable)) {
                        System.out.println(ConsoleColors.RED + "\n✗ Cannot remove " + toRemove + " copies. Only " + 
                                         (currentQuantity - currentAvailable) + " copies are currently issued." + ConsoleColors.RESET);
                        pressEnterToContinue();
                        manageBooks();
                        return;
                    }
                    newAvailable = currentAvailable - toRemove;
                }
            }
            
            String sql = "UPDATE books SET title = COALESCE(NULLIF(?, ''), title), " +
                         "author = COALESCE(NULLIF(?, ''), author), " +
                         "quantity = CASE WHEN ? > 0 THEN ? ELSE quantity END, " +
                         "available_quantity = ? WHERE book_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title.isEmpty() ? null : title);
            stmt.setString(2, author.isEmpty() ? null : author);
            stmt.setInt(3, quantityInput);
            stmt.setInt(4, quantityInput);
            stmt.setInt(5, newAvailable);
            stmt.setInt(6, bookId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println(ConsoleColors.GREEN + "\n✓ Book updated successfully!" + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.RED + "\n✗ Failed to update book." + ConsoleColors.RESET);
            }
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
        manageBooks();
    }

    private static void deleteBook() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                DELETE BOOK                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter Book ID to delete: " + ConsoleColors.RESET);
        int bookId = getIntInput(1, Integer.MAX_VALUE);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if book exists and is not issued
            String checkSql = "SELECT * FROM books WHERE book_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println(ConsoleColors.RED + "\n✗ Book not found with ID: " + bookId + ConsoleColors.RESET);
                pressEnterToContinue();
                manageBooks();
                return;
            }
            
            if (rs.getInt("available_quantity") != rs.getInt("quantity")) {
                System.out.println(ConsoleColors.RED + "\n✗ Cannot delete book. Some copies are currently issued." + ConsoleColors.RESET);
                pressEnterToContinue();
                manageBooks();
                return;
            }
            
            String sql = "DELETE FROM books WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println(ConsoleColors.GREEN + "\n✓ Book deleted successfully!" + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.RED + "\n✗ Failed to delete book." + ConsoleColors.RESET);
            }
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
        manageBooks();
    }

    private static void manageUsers() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                MANAGE USERS                  " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  1. Add New User                            " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  2. View All Users                          " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  3. Update User                             " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  4. Delete User                             " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  5. Back to Admin Menu                      " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter your choice (1-5): " + ConsoleColors.RESET);
        
        int choice = getIntInput(1, 5);
        
        switch (choice) {
            case 1:
                addUser();
                break;
            case 2:
                viewAllUsers();
                break;
            case 3:
                updateUser();
                break;
            case 4:
                deleteUser();
                break;
            case 5:
                return;
        }
    }

    private static void addUser() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                 ADD NEW USER                  " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter Username: " + ConsoleColors.RESET);
        String username = scanner.nextLine();
        
        System.out.print(ConsoleColors.YELLOW + "Enter Password: " + ConsoleColors.RESET);
        String password = scanner.nextLine();
        
        System.out.print(ConsoleColors.YELLOW + "Enter Full Name: " + ConsoleColors.RESET);
        String fullName = scanner.nextLine();
        
        System.out.print(ConsoleColors.YELLOW + "Enter Email: " + ConsoleColors.RESET);
        String email = scanner.nextLine();
        
        System.out.print(ConsoleColors.YELLOW + "Enter Phone: " + ConsoleColors.RESET);
        String phone = scanner.nextLine();
        
        System.out.print(ConsoleColors.YELLOW + "Enter Role (admin/student): " + ConsoleColors.RESET);
        String role = scanner.nextLine();
        
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("student")) {
            System.out.println(ConsoleColors.RED + "\n✗ Role must be either 'admin' or 'student'." + ConsoleColors.RESET);
            pressEnterToContinue();
            manageUsers();
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO users (username, password, role, full_name, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role.toLowerCase());
            stmt.setString(4, fullName);
            stmt.setString(5, email);
            stmt.setString(6, phone);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println(ConsoleColors.GREEN + "\n✓ User added successfully!" + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.RED + "\n✗ Failed to add user." + ConsoleColors.RESET);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println(ConsoleColors.RED + "\n✗ A user with this username already exists." + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
            }
        }
        
        pressEnterToContinue();
        manageUsers();
    }

    private static void viewAllUsers() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                 ALL USERS                    " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users ORDER BY role, username";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (!rs.isBeforeFirst()) {
                System.out.println(ConsoleColors.YELLOW + "\nNo users found in the database." + ConsoleColors.RESET);
                pressEnterToContinue();
                manageUsers();
                return;
            }
            
            System.out.println(ConsoleColors.CYAN + "\n╔══════════╦══════════════════════════════╦══════════════════════════════╦══════════╗");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "ID" + ConsoleColors.CYAN + "       ║ " + ConsoleColors.WHITE_BOLD + "Username" + 
                             ConsoleColors.CYAN + "                    ║ " + ConsoleColors.WHITE_BOLD + "Name" + 
                             ConsoleColors.CYAN + "                       ║ " + ConsoleColors.WHITE_BOLD + "Role" + ConsoleColors.CYAN + "    ║");
            System.out.println("╠══════════╬══════════════════════════════╬══════════════════════════════╬══════════╣");
            
            while (rs.next()) {
                String roleColor = rs.getString("role").equals("admin") ? 
                    ConsoleColors.RED_BOLD : ConsoleColors.GREEN_BOLD;
                
                System.out.println("║ " + ConsoleColors.YELLOW + String.format("%-8s", rs.getInt("user_id")) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + String.format("%-28s", 
                                 truncateString(rs.getString("username"), 28)) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + String.format("%-28s", 
                                 truncateString(rs.getString("full_name"), 28)) + 
                                 ConsoleColors.CYAN + " ║ " + roleColor + 
                                 String.format("%-8s", rs.getString("role")) + 
                                 ConsoleColors.CYAN + " ║");
            }
            
            System.out.println("╚══════════╩══════════════════════════════╩══════════════════════════════╩══════════╝" + ConsoleColors.RESET);
            
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
        manageUsers();
    }

    private static void updateUser() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                UPDATE USER                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter User ID to update: " + ConsoleColors.RESET);
        int userId = getIntInput(1, Integer.MAX_VALUE);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if user exists
            String checkSql = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println(ConsoleColors.RED + "\n✗ User not found with ID: " + userId + ConsoleColors.RESET);
                pressEnterToContinue();
                manageUsers();
                return;
            }
            
            System.out.println(ConsoleColors.CYAN + "\nCurrent User Details:");
            System.out.println("Username: " + rs.getString("username"));
            System.out.println("Name: " + rs.getString("full_name"));
            System.out.println("Role: " + rs.getString("role") + ConsoleColors.RESET);
            
            System.out.print(ConsoleColors.YELLOW + "\nEnter new Username (press Enter to keep current): " + ConsoleColors.RESET);
            String username = scanner.nextLine();
            
            System.out.print(ConsoleColors.YELLOW + "Enter new Password (press Enter to keep current): " + ConsoleColors.RESET);
            String password = scanner.nextLine();
            
            System.out.print(ConsoleColors.YELLOW + "Enter new Full Name (press Enter to keep current): " + ConsoleColors.RESET);
            String fullName = scanner.nextLine();
            
            System.out.print(ConsoleColors.YELLOW + "Enter new Email (press Enter to keep current): " + ConsoleColors.RESET);
            String email = scanner.nextLine();
            
            System.out.print(ConsoleColors.YELLOW + "Enter new Phone (press Enter to keep current): " + ConsoleColors.RESET);
            String phone = scanner.nextLine();
            
            System.out.print(ConsoleColors.YELLOW + "Enter new Role (admin/student, press Enter to keep current): " + ConsoleColors.RESET);
            String role = scanner.nextLine();
            
            if (!role.isEmpty() && !role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("student")) {
                System.out.println(ConsoleColors.RED + "\n✗ Role must be either 'admin' or 'student'." + ConsoleColors.RESET);
                pressEnterToContinue();
                manageUsers();
                return;
            }
            
            String sql = "UPDATE users SET username = COALESCE(NULLIF(?, ''), username), " +
                         "password = COALESCE(NULLIF(?, ''), password), " +
                         "full_name = COALESCE(NULLIF(?, ''), full_name), " +
                         "email = COALESCE(NULLIF(?, ''), email), " +
                         "phone = COALESCE(NULLIF(?, ''), phone), " +
                         "role = COALESCE(NULLIF(?, ''), role) WHERE user_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username.isEmpty() ? null : username);
            stmt.setString(2, password.isEmpty() ? null : password);
            stmt.setString(3, fullName.isEmpty() ? null : fullName);
            stmt.setString(4, email.isEmpty() ? null : email);
            stmt.setString(5, phone.isEmpty() ? null : phone);
            stmt.setString(6, role.isEmpty() ? null : role.toLowerCase());
            stmt.setInt(7, userId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println(ConsoleColors.GREEN + "\n✓ User updated successfully!" + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.RED + "\n✗ Failed to update user." + ConsoleColors.RESET);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println(ConsoleColors.RED + "\n✗ A user with this username already exists." + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
            }
        }
        
        pressEnterToContinue();
        manageUsers();
    }

    private static void deleteUser() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                DELETE USER                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter User ID to delete: " + ConsoleColors.RESET);
        int userId = getIntInput(1, Integer.MAX_VALUE);
        
        if (userId == currentUser.getUserId()) {
            System.out.println(ConsoleColors.RED + "\n✗ You cannot delete your own account." + ConsoleColors.RESET);
            pressEnterToContinue();
            manageUsers();
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if user exists and has no transactions
            String checkSql = "SELECT u.*, COUNT(t.transaction_id) as transaction_count " +
                             "FROM users u LEFT JOIN transactions t ON u.user_id = t.user_id " +
                             "WHERE u.user_id = ? GROUP BY u.user_id";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println(ConsoleColors.RED + "\n✗ User not found with ID: " + userId + ConsoleColors.RESET);
                pressEnterToContinue();
                manageUsers();
                return;
            }
            
            if (rs.getInt("transaction_count") > 0) {
                System.out.println(ConsoleColors.RED + "\n✗ Cannot delete user. User has transaction history." + ConsoleColors.RESET);
                pressEnterToContinue();
                manageUsers();
                return;
            }
            
            String sql = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println(ConsoleColors.GREEN + "\n✓ User deleted successfully!" + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.RED + "\n✗ Failed to delete user." + ConsoleColors.RESET);
            }
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
        manageUsers();
    }

    private static void viewAllTransactions() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "              ALL TRANSACTIONS                " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT t.*, b.title as book_title, u.full_name as user_name " +
                         "FROM transactions t " +
                         "JOIN books b ON t.book_id = b.book_id " +
                         "JOIN users u ON t.user_id = u.user_id " +
                         "ORDER BY t.issue_date DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (!rs.isBeforeFirst()) {
                System.out.println(ConsoleColors.YELLOW + "\nNo transactions found." + ConsoleColors.RESET);
                pressEnterToContinue();
                return;
            }
            
            System.out.println(ConsoleColors.CYAN + "\n╔══════════╦══════════════════════════════╦══════════════════════╦════════════╦════════════╗");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "Trans ID" + ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE_BOLD + "User" + 
                             ConsoleColors.CYAN + "                       ║ " + ConsoleColors.WHITE_BOLD + "Book" + 
                             ConsoleColors.CYAN + "                 ║ " + ConsoleColors.WHITE_BOLD + "Issue Date" + ConsoleColors.CYAN + " ║ " + 
                             ConsoleColors.WHITE_BOLD + "Status" + ConsoleColors.CYAN + "    ║");
            System.out.println("╠══════════╬══════════════════════════════╬══════════════════════╬════════════╬════════════╣");
            
            while (rs.next()) {
                String statusColor;
                String status = rs.getString("status");
                
                if (status.equals("issued")) {
                    statusColor = ConsoleColors.GREEN;
                } else if (status.equals("returned")) {
                    statusColor = ConsoleColors.BLUE;
                } else {
                    statusColor = ConsoleColors.RED;
                }
                
                System.out.println("║ " + ConsoleColors.YELLOW + String.format("%-8s", rs.getInt("transaction_id")) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + String.format("%-28s", 
                                 truncateString(rs.getString("user_name"), 28)) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + String.format("%-20s", 
                                 truncateString(rs.getString("book_title"), 20)) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + 
                                 String.format("%-10s", rs.getDate("issue_date")) + 
                                 ConsoleColors.CYAN + " ║ " + statusColor + 
                                 String.format("%-10s", status) + 
                                 ConsoleColors.CYAN + " ║");
            }
            
            System.out.println("╚══════════╩══════════════════════════════╩══════════════════════╩════════════╩════════════╝" + ConsoleColors.RESET);
            
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
    }

    private static void viewPayments() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                 ALL PAYMENTS                 " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, u.full_name as user_name " +
                         "FROM payments p " +
                         "JOIN users u ON p.user_id = u.user_id " +
                         "ORDER BY p.payment_date DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (!rs.isBeforeFirst()) {
                System.out.println(ConsoleColors.YELLOW + "\nNo payments found." + ConsoleColors.RESET);
                pressEnterToContinue();
                return;
            }
            
            System.out.println(ConsoleColors.CYAN + "\n╔══════════╦══════════════════════════════╦══════════╦══════════════════════╦════════════╗");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "Paym ID" + ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE_BOLD + "User" + 
                             ConsoleColors.CYAN + "                       ║ " + ConsoleColors.WHITE_BOLD + "Amount" + 
                             ConsoleColors.CYAN + "   ║ " + ConsoleColors.WHITE_BOLD + "Type" + ConsoleColors.CYAN + "                 ║ " + 
                             ConsoleColors.WHITE_BOLD + "Date" + ConsoleColors.CYAN + "      ║");
            System.out.println("╠══════════╬══════════════════════════════╬══════════╬══════════════════════╬════════════╣");
            
            double totalAmount = 0;
            
            while (rs.next()) {
                String typeColor = rs.getString("payment_type").equals("fine") ? 
                    ConsoleColors.RED : ConsoleColors.GREEN;
                
                System.out.println("║ " + ConsoleColors.YELLOW + String.format("%-8s", rs.getInt("payment_id")) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + String.format("%-28s", 
                                 truncateString(rs.getString("user_name"), 28)) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + 
                                 String.format("%8.2f", rs.getDouble("amount")) + 
                                 ConsoleColors.CYAN + " ║ " + typeColor + 
                                 String.format("%-20s", rs.getString("payment_type")) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + 
                                 String.format("%-10s", rs.getTimestamp("payment_date").toString().substring(0, 10)) + 
                                 ConsoleColors.CYAN + " ║");
                
                totalAmount += rs.getDouble("amount");
            }
            
            System.out.println("╠══════════╩══════════════════════════════╩══════════╩══════════════════════╩════════════╣");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "Total Revenue: " + 
                             ConsoleColors.YELLOW + String.format("%40.2f", totalAmount) + 
                             ConsoleColors.CYAN + "                          ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════════════╝" + ConsoleColors.RESET);
            
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
    }

    private static void generateReports() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                GENERATE REPORTS               " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  1. Books Report                             " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  2. Users Report                             " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  3. Transactions Report                      " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  4. Financial Report                         " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("║" + ConsoleColors.WHITE_BOLD + "  5. Back to Admin Menu                       " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter your choice (1-5): " + ConsoleColors.RESET);
        
        int choice = getIntInput(1, 5);
        
        switch (choice) {
            case 1:
                generateBooksReport();
                break;
            case 2:
                generateUsersReport();
                break;
            case 3:
                generateTransactionsReport();
                break;
            case 4:
                generateFinancialReport();
                break;
            case 5:
                return;
        }
    }

    private static void generateBooksReport() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                BOOKS REPORT                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT genre, COUNT(*) as count, SUM(quantity) as total, SUM(available_quantity) as available " +
                         "FROM books GROUP BY genre ORDER BY count DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println(ConsoleColors.CYAN + "\n╔══════════════════════════════╦══════════╦══════════╦══════════╗");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "Genre" + ConsoleColors.CYAN + "                     ║ " + ConsoleColors.WHITE_BOLD + "Titles" + 
                             ConsoleColors.CYAN + "   ║ " + ConsoleColors.WHITE_BOLD + "Total" + 
                             ConsoleColors.CYAN + "    ║ " + ConsoleColors.WHITE_BOLD + "Available" + ConsoleColors.CYAN + " ║");
            System.out.println("╠══════════════════════════════╬══════════╬══════════╬══════════╣");
            
            int totalTitles = 0;
            int totalBooks = 0;
            int totalAvailable = 0;
            
            while (rs.next()) {
                System.out.println("║ " + ConsoleColors.WHITE + String.format("%-28s", 
                                 truncateString(rs.getString("genre"), 28)) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.YELLOW + 
                                 String.format("%6s", rs.getInt("count")) + 
                                 ConsoleColors.CYAN + "   ║ " + ConsoleColors.YELLOW + 
                                 String.format("%6s", rs.getInt("total")) + 
                                 ConsoleColors.CYAN + "   ║ " + ConsoleColors.YELLOW + 
                                 String.format("%6s", rs.getInt("available")) + 
                                 ConsoleColors.CYAN + "   ║");
                
                totalTitles += rs.getInt("count");
                totalBooks += rs.getInt("total");
                totalAvailable += rs.getInt("available");
            }
            
            System.out.println("╠══════════════════════════════╬══════════╬══════════╬══════════╣");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "Total" + ConsoleColors.CYAN + "                      ║ " + 
                             ConsoleColors.YELLOW_BOLD + String.format("%6s", totalTitles) + 
                             ConsoleColors.CYAN + "   ║ " + ConsoleColors.YELLOW_BOLD + 
                             String.format("%6s", totalBooks) + 
                             ConsoleColors.CYAN + "   ║ " + ConsoleColors.YELLOW_BOLD + 
                             String.format("%6s", totalAvailable) + 
                             ConsoleColors.CYAN + "   ║");
            System.out.println("╚══════════════════════════════╩══════════╩══════════╩══════════╝" + ConsoleColors.RESET);
            
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
        generateReports();
    }

    private static void generateUsersReport() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                USERS REPORT                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT role, COUNT(*) as count FROM users GROUP BY role";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println(ConsoleColors.CYAN + "\n╔══════════╦══════════╗");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "Role" + ConsoleColors.CYAN + "     ║ " + ConsoleColors.WHITE_BOLD + "Count" + 
                             ConsoleColors.CYAN + "   ║");
            System.out.println("╠══════════╬══════════╣");
            
            int totalUsers = 0;
            
            while (rs.next()) {
                String roleColor = rs.getString("role").equals("admin") ? 
                    ConsoleColors.RED : ConsoleColors.GREEN;
                
                System.out.println("║ " + roleColor + String.format("%-8s", rs.getString("role")) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.YELLOW + 
                                 String.format("%6s", rs.getInt("count")) + 
                                 ConsoleColors.CYAN + "   ║");
                
                totalUsers += rs.getInt("count");
            }
            
            System.out.println("╠══════════╬══════════╣");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "Total" + ConsoleColors.CYAN + "   ║ " + 
                             ConsoleColors.YELLOW_BOLD + String.format("%6s", totalUsers) + 
                             ConsoleColors.CYAN + "   ║");
            System.out.println("╚══════════╩══════════╝" + ConsoleColors.RESET);
            
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
        generateReports();
    }

    private static void generateTransactionsReport() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "            TRANSACTIONS REPORT                " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT status, COUNT(*) as count FROM transactions GROUP BY status";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println(ConsoleColors.CYAN + "\n╔════════════╦══════════╗");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "Status" + ConsoleColors.CYAN + "     ║ " + ConsoleColors.WHITE_BOLD + "Count" + 
                             ConsoleColors.CYAN + "   ║");
            System.out.println("╠════════════╬══════════╣");
            
            int totalTransactions = 0;
            
            while (rs.next()) {
                String statusColor;
                String status = rs.getString("status");
                
                if (status.equals("issued")) {
                    statusColor = ConsoleColors.GREEN;
                } else if (status.equals("returned")) {
                    statusColor = ConsoleColors.BLUE;
                } else {
                    statusColor = ConsoleColors.RED;
                }
                
                System.out.println("║ " + statusColor + String.format("%-10s", status) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.YELLOW + 
                                 String.format("%6s", rs.getInt("count")) + 
                                 ConsoleColors.CYAN + "   ║");
                
                totalTransactions += rs.getInt("count");
            }
            
            System.out.println("╠════════════╬══════════╣");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "Total" + ConsoleColors.CYAN + "      ║ " + 
                             ConsoleColors.YELLOW_BOLD + String.format("%6s", totalTransactions) + 
                             ConsoleColors.CYAN + "   ║");
            System.out.println("╚════════════╩══════════╝" + ConsoleColors.RESET);
            
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
        generateReports();
    }

    private static void generateFinancialReport() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "             FINANCIAL REPORT                 " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total fines
            String finesSql = "SELECT COALESCE(SUM(fine_amount), 0) as total_fines FROM transactions WHERE fine_amount > 0";
            Statement finesStmt = conn.createStatement();
            ResultSet finesRs = finesStmt.executeQuery(finesSql);
            finesRs.next();
            double totalFines = finesRs.getDouble("total_fines");
            
            // Collected fines
            String collectedSql = "SELECT COALESCE(SUM(amount), 0) as collected_fines FROM payments WHERE payment_type = 'fine'";
            Statement collectedStmt = conn.createStatement();
            ResultSet collectedRs = collectedStmt.executeQuery(collectedSql);
            collectedRs.next();
            double collectedFines = collectedRs.getDouble("collected_fines");
            
            // Membership fees
            String membershipSql = "SELECT COALESCE(SUM(amount), 0) as membership_fees FROM payments WHERE payment_type = 'membership'";
            Statement membershipStmt = conn.createStatement();
            ResultSet membershipRs = membershipStmt.executeQuery(membershipSql);
            membershipRs.next();
            double membershipFees = membershipRs.getDouble("membership_fees");
            
            // Total revenue
            double totalRevenue = collectedFines + membershipFees;
            
            System.out.println(ConsoleColors.CYAN + "\n╔══════════════════════════════════╦══════════════╗");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "Metric" + ConsoleColors.CYAN + "                         ║ " + ConsoleColors.WHITE_BOLD + "Amount" + 
                             ConsoleColors.CYAN + "       ║");
            System.out.println("╠══════════════════════════════════╬══════════════╣");
            
            System.out.println("║ " + ConsoleColors.WHITE + "Total Fines Issued" + 
                             ConsoleColors.CYAN + "               ║ " + ConsoleColors.YELLOW + 
                             String.format("%10.2f", totalFines) + 
                             ConsoleColors.CYAN + "   ║");
            
            System.out.println("║ " + ConsoleColors.WHITE + "Fines Collected" + 
                             ConsoleColors.CYAN + "                 ║ " + ConsoleColors.YELLOW + 
                             String.format("%10.2f", collectedFines) + 
                             ConsoleColors.CYAN + "   ║");
            
            System.out.println("║ " + ConsoleColors.WHITE + "Outstanding Fines" + 
                             ConsoleColors.CYAN + "                ║ " + ConsoleColors.YELLOW + 
                             String.format("%10.2f", (totalFines - collectedFines)) + 
                             ConsoleColors.CYAN + "   ║");
            
            System.out.println("║ " + ConsoleColors.WHITE + "Membership Fees Collected" + 
                             ConsoleColors.CYAN + "           ║ " + ConsoleColors.YELLOW + 
                             String.format("%10.2f", membershipFees) + 
                             ConsoleColors.CYAN + "   ║");
            
            System.out.println("║ " + ConsoleColors.WHITE + "Total Revenue" + 
                             ConsoleColors.CYAN + "                  ║ " + ConsoleColors.YELLOW_BOLD + 
                             String.format("%10.2f", totalRevenue) + 
                             ConsoleColors.CYAN + "   ║");
            
            System.out.println("╚══════════════════════════════════╩══════════════╝" + ConsoleColors.RESET);
            
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
        generateReports();
    }

    private static void browseBooks() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                BROWSE BOOKS                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM books WHERE available_quantity > 0 ORDER BY title";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (!rs.isBeforeFirst()) {
                System.out.println(ConsoleColors.YELLOW + "\nNo books available for borrowing." + ConsoleColors.RESET);
                pressEnterToContinue();
                return;
            }
            
            System.out.println(ConsoleColors.CYAN + "\n╔══════════╦══════════════════════════════════════╦══════════════════════╦══════════╗");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "ID" + ConsoleColors.CYAN + "       ║ " + ConsoleColors.WHITE_BOLD + "Title" + 
                             ConsoleColors.CYAN + "                           ║ " + ConsoleColors.WHITE_BOLD + "Author" + 
                             ConsoleColors.CYAN + "              ║ " + ConsoleColors.WHITE_BOLD + "Available" + ConsoleColors.CYAN + " ║");
            System.out.println("╠══════════╬══════════════════════════════════════╬══════════════════════╬══════════╣");
            
            while (rs.next()) {
                System.out.println("║ " + ConsoleColors.YELLOW + String.format("%-8s", rs.getInt("book_id")) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + String.format("%-36s", 
                                 truncateString(rs.getString("title"), 36)) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + String.format("%-20s", 
                                 truncateString(rs.getString("author"), 20)) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.GREEN + 
                                 String.format("%5s", rs.getInt("available_quantity")) + 
                                 ConsoleColors.CYAN + "   ║");
            }
            
            System.out.println("╚══════════╩══════════════════════════════════════╩══════════════════════╩══════════╝" + ConsoleColors.RESET);
            
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
    }

    private static void issueBook() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                 ISSUE BOOK                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        // Check if student has any overdue books
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkOverdueSql = "SELECT COUNT(*) as overdue_count FROM transactions " +
                                    "WHERE user_id = ? AND status = 'overdue'";
            PreparedStatement checkOverdueStmt = conn.prepareStatement(checkOverdueSql);
            checkOverdueStmt.setInt(1, currentUser.getUserId());
            ResultSet overdueRs = checkOverdueStmt.executeQuery();
            overdueRs.next();
            
            if (overdueRs.getInt("overdue_count") > 0) {
                System.out.println(ConsoleColors.RED + "\n✗ You have overdue books. Please return them before issuing new ones." + ConsoleColors.RESET);
                pressEnterToContinue();
                return;
            }
            
            // Check if student has reached maximum issue limit (3 books)
            String checkLimitSql = "SELECT COUNT(*) as issued_count FROM transactions " +
                                  "WHERE user_id = ? AND status IN ('issued', 'overdue')";
            PreparedStatement checkLimitStmt = conn.prepareStatement(checkLimitSql);
            checkLimitStmt.setInt(1, currentUser.getUserId());
            ResultSet limitRs = checkLimitStmt.executeQuery();
            limitRs.next();
            
            if (limitRs.getInt("issued_count") >= 3) {
                System.out.println(ConsoleColors.RED + "\n✗ You have reached the maximum limit of 3 books." + ConsoleColors.RESET);
                pressEnterToContinue();
                return;
            }
            
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
            pressEnterToContinue();
            return;
        }
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter Book ID to issue: " + ConsoleColors.RESET);
        int bookId = getIntInput(1, Integer.MAX_VALUE);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if book exists and is available
            String checkBookSql = "SELECT * FROM books WHERE book_id = ? AND available_quantity > 0";
            PreparedStatement checkBookStmt = conn.prepareStatement(checkBookSql);
            checkBookStmt.setInt(1, bookId);
            ResultSet bookRs = checkBookStmt.executeQuery();
            
            if (!bookRs.next()) {
                System.out.println(ConsoleColors.RED + "\n✗ Book not available or not found." + ConsoleColors.RESET);
                pressEnterToContinue();
                return;
            }
            
            // Calculate due date (14 days from now)
            LocalDate issueDate = LocalDate.now();
            LocalDate dueDate = issueDate.plusDays(14);
            
            // Issue the book
            String issueSql = "INSERT INTO transactions (user_id, book_id, issue_date, due_date, status) VALUES (?, ?, ?, ?, 'issued')";
            PreparedStatement issueStmt = conn.prepareStatement(issueSql);
            issueStmt.setInt(1, currentUser.getUserId());
            issueStmt.setInt(2, bookId);
            issueStmt.setDate(3, Date.valueOf(issueDate));
            issueStmt.setDate(4, Date.valueOf(dueDate));
            
            int rowsAffected = issueStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update available quantity
                String updateSql = "UPDATE books SET available_quantity = available_quantity - 1 WHERE book_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();
                
                System.out.println(ConsoleColors.GREEN + "\n✓ Book issued successfully!" + ConsoleColors.RESET);
                System.out.println(ConsoleColors.CYAN + "Due date: " + dueDate + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.RED + "\n✗ Failed to issue book." + ConsoleColors.RESET);
            }
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
    }

    private static void returnBook() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                RETURN BOOK                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        // Show student's issued books
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT t.transaction_id, b.title, t.issue_date, t.due_date, t.fine_amount " +
                         "FROM transactions t " +
                         "JOIN books b ON t.book_id = b.book_id " +
                         "WHERE t.user_id = ? AND t.status IN ('issued', 'overdue') " +
                         "ORDER BY t.due_date";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUser.getUserId());
            ResultSet rs = stmt.executeQuery();
            
            if (!rs.isBeforeFirst()) {
                System.out.println(ConsoleColors.YELLOW + "\nYou don't have any books to return." + ConsoleColors.RESET);
                pressEnterToContinue();
                return;
            }
            
            System.out.println(ConsoleColors.CYAN + "\n╔══════════╦══════════════════════════════════════╦════════════╦════════════╦══════════╗");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "Trans ID" + ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE_BOLD + "Title" + 
                             ConsoleColors.CYAN + "                           ║ " + ConsoleColors.WHITE_BOLD + "Issue Date" + 
                             ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE_BOLD + "Due Date" + ConsoleColors.CYAN + "  ║ " + 
                             ConsoleColors.WHITE_BOLD + "Fine" + ConsoleColors.CYAN + "     ║");
            System.out.println("╠══════════╬══════════════════════════════════════╬════════════╬════════════╬══════════╣");
            
            while (rs.next()) {
                double fine = rs.getDouble("fine_amount");
                String fineColor = fine > 0 ? ConsoleColors.RED : ConsoleColors.GREEN;
                String fineText = fine > 0 ? String.format("%.2f", fine) : "None";
                
                System.out.println("║ " + ConsoleColors.YELLOW + String.format("%-8s", rs.getInt("transaction_id")) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + String.format("%-36s", 
                                 truncateString(rs.getString("title"), 36)) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + 
                                 String.format("%-10s", rs.getDate("issue_date")) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + 
                                 String.format("%-10s", rs.getDate("due_date")) + 
                                 ConsoleColors.CYAN + " ║ " + fineColor + 
                                 String.format("%-8s", fineText) + 
                                 ConsoleColors.CYAN + " ║");
            }
            
            System.out.println("╚══════════╩══════════════════════════════════════╩════════════╩════════════╩══════════╝" + ConsoleColors.RESET);
            
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
            pressEnterToContinue();
            return;
        }
        
        System.out.print(ConsoleColors.YELLOW + "\nEnter Transaction ID to return: " + ConsoleColors.RESET);
        int transactionId = getIntInput(1, Integer.MAX_VALUE);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get transaction details
            String getSql = "SELECT t.*, b.book_id, b.title FROM transactions t " +
                           "JOIN books b ON t.book_id = b.book_id " +
                           "WHERE t.transaction_id = ? AND t.user_id = ?";
            PreparedStatement getStmt = conn.prepareStatement(getSql);
            getStmt.setInt(1, transactionId);
            getStmt.setInt(2, currentUser.getUserId());
            ResultSet rs = getStmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println(ConsoleColors.RED + "\n✗ Transaction not found or you don't have permission to return it." + ConsoleColors.RESET);
                pressEnterToContinue();
                return;
            }
            
            int bookId = rs.getInt("book_id");
            double fineAmount = rs.getDouble("fine_amount");
            Date dueDate = rs.getDate("due_date");
            
            // Calculate additional fine if overdue
            LocalDate returnDate = LocalDate.now();
            LocalDate dueLocalDate = dueDate.toLocalDate();
            
            if (returnDate.isAfter(dueLocalDate)) {
                long daysOverdue = ChronoUnit.DAYS.between(dueLocalDate, returnDate);
                fineAmount += daysOverdue * FINE_PER_DAY;
                
                System.out.println(ConsoleColors.RED + "This book is " + daysOverdue + " days overdue." + ConsoleColors.RESET);
                System.out.println(ConsoleColors.RED + "Additional fine: $" + (daysOverdue * FINE_PER_DAY) + ConsoleColors.RESET);
            }
            
            // Update transaction
            String returnSql = "UPDATE transactions SET return_date = ?, fine_amount = ?, " +
                              "status = CASE WHEN fine_amount > 0 THEN 'overdue' ELSE 'returned' END " +
                              "WHERE transaction_id = ?";
            PreparedStatement returnStmt = conn.prepareStatement(returnSql);
            returnStmt.setDate(1, Date.valueOf(returnDate));
            returnStmt.setDouble(2, fineAmount);
            returnStmt.setInt(3, transactionId);
            
            int rowsAffected = returnStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update available quantity
                String updateSql = "UPDATE books SET available_quantity = available_quantity + 1 WHERE book_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();
                
                System.out.println(ConsoleColors.GREEN + "\n✓ Book returned successfully!" + ConsoleColors.RESET);
                
                if (fineAmount > 0) {
                    System.out.println(ConsoleColors.RED + "Total fine: $" + fineAmount + ConsoleColors.RESET);
                    System.out.println(ConsoleColors.YELLOW + "Please pay your fine at the payment section." + ConsoleColors.RESET);
                }
            } else {
                System.out.println(ConsoleColors.RED + "\n✗ Failed to return book." + ConsoleColors.RESET);
            }
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
    }

    private static void viewMyTransactions() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "             MY TRANSACTIONS                   " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT t.*, b.title as book_title " +
                         "FROM transactions t " +
                         "JOIN books b ON t.book_id = b.book_id " +
                         "WHERE t.user_id = ? " +
                         "ORDER BY t.issue_date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUser.getUserId());
            ResultSet rs = stmt.executeQuery();
            
            if (!rs.isBeforeFirst()) {
                System.out.println(ConsoleColors.YELLOW + "\nNo transactions found." + ConsoleColors.RESET);
                pressEnterToContinue();
                return;
            }
            
            System.out.println(ConsoleColors.CYAN + "\n╔══════════╦══════════════════════════════════════╦════════════╦════════════╦════════════╦══════════╗");
            System.out.println("║ " + ConsoleColors.WHITE_BOLD + "Trans ID" + ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE_BOLD + "Book" + 
                             ConsoleColors.CYAN + "                           ║ " + ConsoleColors.WHITE_BOLD + "Issue Date" + 
                             ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE_BOLD + "Due Date" + ConsoleColors.CYAN + "  ║ " + 
                             ConsoleColors.WHITE_BOLD + "Return Date" + ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE_BOLD + "Status" + ConsoleColors.CYAN + "    ║");
            System.out.println("╠══════════╬══════════════════════════════════════╬════════════╬════════════╬════════════╬════════════╣");
            
            while (rs.next()) {
                String statusColor;
                String status = rs.getString("status");
                
                if (status.equals("issued")) {
                    statusColor = ConsoleColors.GREEN;
                } else if (status.equals("returned")) {
                    statusColor = ConsoleColors.BLUE;
                } else {
                    statusColor = ConsoleColors.RED;
                }
                
                System.out.println("║ " + ConsoleColors.YELLOW + String.format("%-8s", rs.getInt("transaction_id")) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + String.format("%-36s", 
                                 truncateString(rs.getString("book_title"), 36)) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + 
                                 String.format("%-10s", rs.getDate("issue_date")) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + 
                                 String.format("%-10s", rs.getDate("due_date")) + 
                                 ConsoleColors.CYAN + " ║ " + ConsoleColors.WHITE + 
                                 String.format("%-10s", rs.getDate("return_date") != null ? rs.getDate("return_date") : "Not returned") + 
                                 ConsoleColors.CYAN + " ║ " + statusColor + 
                                 String.format("%-10s", status) + 
                                 ConsoleColors.CYAN + " ║");
            }
            
            System.out.println("╚══════════╩══════════════════════════════════════╩════════════╩════════════╩════════════╩════════════╝" + ConsoleColors.RESET);
            
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
    }

    private static void payFines() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD + "╔══════════════════════════════════════════════════╗");
        System.out.println("║" + ConsoleColors.CYAN_BOLD + "                 PAY FINES                    " + ConsoleColors.BLUE_BOLD + "║");
        System.out.println("╚══════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Calculate total fines
            String finesSql = "SELECT COALESCE(SUM(fine_amount), 0) as total_fines " +
                             "FROM transactions " +
                             "WHERE user_id = ? AND status = 'overdue'";
            PreparedStatement finesStmt = conn.prepareStatement(finesSql);
            finesStmt.setInt(1, currentUser.getUserId());
            ResultSet finesRs = finesStmt.executeQuery();
            finesRs.next();
            
            double totalFines = finesRs.getDouble("total_fines");
            
            if (totalFines <= 0) {
                System.out.println(ConsoleColors.GREEN + "\nYou don't have any fines to pay." + ConsoleColors.RESET);
                pressEnterToContinue();
                return;
            }
            
            System.out.println(ConsoleColors.RED + "\nTotal fines due: $" + totalFines + ConsoleColors.RESET);
            System.out.print(ConsoleColors.YELLOW + "Enter amount to pay: $" + ConsoleColors.RESET);
            double amount = getDoubleInput(0, totalFines);
            
            if (amount <= 0) {
                System.out.println(ConsoleColors.RED + "\nPayment cancelled." + ConsoleColors.RESET);
                pressEnterToContinue();
                return;
            }
            
            // Record payment
            String paymentSql = "INSERT INTO payments (user_id, amount, payment_type, description) VALUES (?, ?, 'fine', ?)";
            PreparedStatement paymentStmt = conn.prepareStatement(paymentSql);
            paymentStmt.setInt(1, currentUser.getUserId());
            paymentStmt.setDouble(2, amount);
            paymentStmt.setString(3, "Fine payment for overdue books");
            
            int rowsAffected = paymentStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update transactions to reduce fines
                String updateSql = "UPDATE transactions SET fine_amount = fine_amount - ? " +
                                  "WHERE user_id = ? AND status = 'overdue'";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setDouble(1, amount);
                updateStmt.setInt(2, currentUser.getUserId());
                updateStmt.executeUpdate();
                
                System.out.println(ConsoleColors.GREEN + "\n✓ Payment of $" + amount + " processed successfully!" + ConsoleColors.RESET);
                
                // Check if all fines are paid and update status
                String checkSql = "SELECT COUNT(*) as overdue_count FROM transactions " +
                                 "WHERE user_id = ? AND status = 'overdue' AND fine_amount <= 0";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setInt(1, currentUser.getUserId());
                ResultSet checkRs = checkStmt.executeQuery();
                checkRs.next();
                
                if (checkRs.getInt("overdue_count") > 0) {
                    String statusSql = "UPDATE transactions SET status = 'returned' " +
                                      "WHERE user_id = ? AND status = 'overdue' AND fine_amount <= 0";
                    PreparedStatement statusStmt = conn.prepareStatement(statusSql);
                    statusStmt.setInt(1, currentUser.getUserId());
                    statusStmt.executeUpdate();
                }
            } else {
                System.out.println(ConsoleColors.RED + "\n✗ Payment failed." + ConsoleColors.RESET);
            }
        } catch (SQLException e) {
            System.out.println(ConsoleColors.RED + "\n✗ Database error: " + e.getMessage() + ConsoleColors.RESET);
        }
        
        pressEnterToContinue();
    }

    private static int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.print(ConsoleColors.RED + "Please enter a number between " + min + " and " + max + ": " + ConsoleColors.RESET);
                }
            } catch (InputMismatchException e) {
                scanner.nextLine(); // Clear invalid input
                System.out.print(ConsoleColors.RED + "Please enter a valid number: " + ConsoleColors.RESET);
            }
        }
    }

    private static double getDoubleInput(double min, double max) {
        while (true) {
            try {
                double input = scanner.nextDouble();
                scanner.nextLine(); // Consume newline
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.print(ConsoleColors.RED + "Please enter a number between " + min + " and " + max + ": " + ConsoleColors.RESET);
                }
            } catch (InputMismatchException e) {
                scanner.nextLine(); // Clear invalid input
                System.out.print(ConsoleColors.RED + "Please enter a valid number: " + ConsoleColors.RESET);
            }
        }
    }

    private static void pressEnterToContinue() {
        System.out.print(ConsoleColors.YELLOW + "\nPress Enter to continue..." + ConsoleColors.RESET);
        scanner.nextLine();
    }

    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // If clearing screen fails, just print some empty lines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    private static String truncateString(String str, int length) {
        if (str == null) return "";
        if (str.length() <= length) {
            return str;
        } else {
            return str.substring(0, length - 3) + "...";
        }
    }
}


	


