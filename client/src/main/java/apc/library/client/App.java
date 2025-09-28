package apc.library.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Scanner;

public class App {
    private static final String USER_SERVICE_URL = "http://localhost:8082/api/auth";
    private static final String BOOK_SERVICE_URL = "http://localhost:8081/api/books";
    private static String jwtToken = null; // This will store the token after login

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RestTemplate restTemplate = new RestTemplate();

        while (true) {
            System.out.println("\n--- Library Menu ---");
            if (jwtToken == null) {
                System.out.println("1. Register");
                System.out.println("2. Login");
            } else {
                System.out.println("3. View All Books");
                System.out.println("4. Borrow a Book");
                System.out.println("5. Logout");
            }
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice == 0) {
                System.out.println("Exiting...");
                break; // Exit the while loop
            }

            if (jwtToken == null) {
                switch (choice) {
                    case 1 -> register(scanner, restTemplate);
                    case 2 -> login(scanner, restTemplate);
                    default -> System.out.println("Invalid choice. Please log in or register.");
                }
            } else {
                 switch (choice) {
                    case 3 -> viewBooks(restTemplate);
                    case 4 -> borrowBook(scanner, restTemplate);
                    case 5 -> { jwtToken = null; System.out.println("Logged out successfully."); }
                    default -> System.out.println("Invalid choice.");
                }
            }
        }
        scanner.close();
    }

    private static void register(Scanner scanner, RestTemplate restTemplate) {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter role (e.g., ROLE_USER or ROLE_ADMIN): ");
        String role = scanner.nextLine();

        String requestBody = String.format(
            "{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
            name, email, password, role
        );
        
        try {
            ResponseEntity<String> response = sendPostRequest(USER_SERVICE_URL + "/register", requestBody, null);
            System.out.println("Server Response: " + response.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println("Error during registration: " + e.getResponseBodyAsString());
        }
    }

    private static void login(Scanner scanner, RestTemplate restTemplate) {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        String requestBody = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        
        try {
            ResponseEntity<String> response = sendPostRequest(USER_SERVICE_URL + "/login", requestBody, null);
            if (response.getStatusCode().is2xxSuccessful()) {
                jwtToken = response.getBody();
                System.out.println("Login successful!");
            }
        } catch (HttpClientErrorException e) {
             System.out.println("Login failed: " + e.getResponseBodyAsString());
        }
    }

    private static void viewBooks(RestTemplate restTemplate) {
        try {
            ResponseEntity<String> response = sendGetRequest(BOOK_SERVICE_URL, jwtToken);
            System.out.println("Available Books:\n" + response.getBody());
        } catch (HttpClientErrorException e) {
             System.out.println("Error fetching books: " + e.getResponseBodyAsString());
        }
    }

    private static void borrowBook(Scanner scanner, RestTemplate restTemplate) {
        System.out.print("Enter the ID of the book you want to borrow: ");
        long bookId = scanner.nextLong();
        scanner.nextLine(); // consume newline
        
        try {
            ResponseEntity<String> response = sendPostRequest(BOOK_SERVICE_URL + "/" + bookId + "/borrow", null, jwtToken);
            System.out.println("Book borrowed successfully! Updated book details: " + response.getBody());
        } catch (HttpClientErrorException e) {
             System.out.println("Error borrowing book: " + e.getResponseBodyAsString());
        }
    }

    // --- Helper methods for making API calls ---
    private static ResponseEntity<String> sendPostRequest(String url, String body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return new RestTemplate().postForEntity(url, entity, String.class);
    }
    
    private static ResponseEntity<String> sendGetRequest(String url, String token) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return new RestTemplate().exchange(url, HttpMethod.GET, entity, String.class);
    }
}