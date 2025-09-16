package apc.library_management;

import apc.library_management.model.Book;
import apc.library_management.model.User;
import apc.library_management.service.BookService;
import apc.library_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class LibraryManagementApplication implements CommandLineRunner {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nSelect Role:");
            System.out.println("1. User");
            System.out.println("2. Admin");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            int roleChoice = Integer.parseInt(scanner.nextLine());

            if (roleChoice == 3) {
                System.out.println("Exiting application...");
                System.exit(0);
            }

            switch (roleChoice) {
                case 1 -> userMenu(scanner);
                case 2 -> adminMenu(scanner);
                default -> System.out.println("Invalid choice, please select again.");
            }
        }
    }

    private void userMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nUser Menu:");
            System.out.println("1. View All Users");
            System.out.println("2. Borrow Book");
            System.out.println("3. Exit to Role Selection");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> {
                    List<User> users = userService.getAllUsers();
                    if (users.isEmpty()) {
                        System.out.println("No users found.");
                    } else {
                        users.forEach(u -> System.out.println("ID: " + u.getId() + ", Name: " + u.getName() + ", Role: " + u.getRole()));
                    }
                }
                case 2 -> {
                    System.out.print("Enter Book ID to borrow: ");
                    Long bookId = Long.parseLong(scanner.nextLine());
                    System.out.print("Enter your User ID: ");
                    Long userId = Long.parseLong(scanner.nextLine());
                    String response = bookService.borrowBook(bookId, userId);
                    System.out.println(response);
                }
                case 3 -> {
                    System.out.println("Returning to role selection...");
                    return;
                }
                default -> System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private void adminMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Add Book");
            System.out.println("2. List Books");
            System.out.println("3. Update Book");
            System.out.println("4. Delete Book");
            System.out.println("5. Add User");
            System.out.println("6. Update User");
            System.out.println("7. Delete User");
            System.out.println("8. Exit to Role Selection");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> {
                    Book book = new Book();
                    System.out.print("Enter title: ");
                    book.setTitle(scanner.nextLine());
                    System.out.print("Enter author: ");
                    book.setAuthor(scanner.nextLine());
                    System.out.print("Enter available copies: ");
                    book.setAvailableCopies(Integer.parseInt(scanner.nextLine()));
                    bookService.addBook(book);
                    System.out.println("Book added successfully.");
                }
                case 2 -> {
                    List<Book> books = bookService.getAllBooks();
                    if (books.isEmpty()) {
                        System.out.println("No books found.");
                    } else {
                        books.forEach(b -> System.out.println("ID: " + b.getId() + ", Title: " + b.getTitle() + ", Author: " + b.getAuthor() + ", Copies: " + b.getAvailableCopies()));
                    }
                }
                case 3 -> {
                    System.out.print("Enter book ID to update: ");
                    Long updateId = Long.parseLong(scanner.nextLine());
                    Book bookDetails = new Book();
                    System.out.print("Enter new title: ");
                    bookDetails.setTitle(scanner.nextLine());
                    System.out.print("Enter new author: ");
                    bookDetails.setAuthor(scanner.nextLine());
                    System.out.print("Enter new available copies: ");
                    bookDetails.setAvailableCopies(Integer.parseInt(scanner.nextLine()));
                    Book updatedBook = bookService.updateBook(updateId, bookDetails);
                    if (updatedBook != null) {
                        System.out.println("Book updated successfully.");
                    } else {
                        System.out.println("Book not found.");
                    }
                }
                case 4 -> {
                    System.out.print("Enter book ID to delete: ");
                    Long deleteId = Long.parseLong(scanner.nextLine());
                    bookService.deleteBook(deleteId);
                    System.out.println("Book deleted if existed.");
                }
                case 5 -> {
                    User user = new User();
                    System.out.print("Enter user name: ");
                    user.setName(scanner.nextLine());
                    System.out.print("Enter user role: ");
                    user.setRole(scanner.nextLine());
                    userService.addUser(user);
                    System.out.println("User added successfully.");
                }
                case 6 -> {
                    System.out.print("Enter user ID to update: ");
                    Long updateUserId = Long.parseLong(scanner.nextLine());
                    User userDetails = new User();
                    System.out.print("Enter new name: ");
                    userDetails.setName(scanner.nextLine());
                    System.out.print("Enter new role: ");
                    userDetails.setRole(scanner.nextLine());
                    try {
                        User updatedUser = userService.updateUser(updateUserId, userDetails);
                        System.out.println("User updated successfully.");
                    } catch (RuntimeException e) {
                        System.out.println("User not found.");
                    }
                }
                case 7 -> {
                    System.out.print("Enter user ID to delete: ");
                    Long deleteUserId = Long.parseLong(scanner.nextLine());
                    userService.deleteUser(deleteUserId);
                    System.out.println("User deleted if existed.");
                }
                case 8 -> {
                    System.out.println("Returning to role selection...");
                    return;
                }
                default ->{
					System.out.println("Invalid choice, please try again.");
				}
            }
        }
    }
}
