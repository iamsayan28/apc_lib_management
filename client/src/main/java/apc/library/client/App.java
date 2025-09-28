package apc.library.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Scanner;

class Book {
    private long id;
    private String title;
    private String author;
    private int availableCopies;

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
}

public class App {
    private static final String USER_SERVICE_URL = "http://localhost:8082/api/auth";
    private static final String BOOK_SERVICE_URL = "http://localhost:8081/api/books";
    private static String jwtToken = null;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RestTemplate restTemplate = new RestTemplate();

        while (true) {
            System.out.println("\n--- Library Menu ---");
            if (jwtToken == null) {
                System.out.println("1. Register");
                System.out.println("2. Login");
            } else {
                String role = getRoleFromToken(jwtToken);
                if ("ROLE_ADMIN".equals(role)) {
                    System.out.println("1. View All Books");
                    System.out.println("2. Add a Book");
                    System.out.println("3. Update a Book");
                    System.out.println("4. Delete a Book");
                    System.out.println("5. Logout");
                } else {
                    System.out.println("1. View All Books");
                    System.out.println("2. Borrow a Book");
                    System.out.println("3. Logout");
                }
            }
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) {
                System.out.println("Exiting...");
                break;
            }

            if (jwtToken == null) {
                switch (choice) {
                    case 1:
                        register(scanner, restTemplate);
                        break;
                    case 2:
                        login(scanner, restTemplate);
                        break;
                    default:
                        System.out.println("Invalid choice. Please log in or register.");
                }
            } else {
                String role = getRoleFromToken(jwtToken);
                if ("ROLE_ADMIN".equals(role)) {
                    switch (choice) {
                        case 1:
                            viewBooks(restTemplate);
                            break;
                        case 2:
                            addBook(scanner, restTemplate);
                            break;
                        case 3:
                            updateBook(scanner, restTemplate);
                            break;
                        case 4:
                            deleteBook(scanner, restTemplate);
                            break;
                        case 5:
                            jwtToken = null;
                            System.out.println("Logged out successfully.");
                            break;
                        default:
                            System.out.println("Invalid choice.");
                    }
                } else {
                    switch (choice) {
                        case 1:
                            viewBooks(restTemplate);
                            break;
                        case 2:
                            borrowBook(scanner, restTemplate);
                            break;
                        case 3:
                            jwtToken = null;
                            System.out.println("Logged out successfully.");
                            break;
                        default:
                            System.out.println("Invalid choice.");
                    }
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
            String responseBody = response.getBody();
            if (responseBody == null || responseBody.trim().isEmpty() || "[]".equals(responseBody.trim())) {
                System.out.println("No books available at the moment.");
            } else {
                ObjectMapper mapper = new ObjectMapper();
                Book[] books = mapper.readValue(responseBody, Book[].class);
                System.out.println("Available Books:");
                System.out.println("---------------------------------------------------------");
                System.out.printf("%-5s | %-20s | %-15s | %s%n", "ID", "Title", "Author", "Copies");
                System.out.println("---------------------------------------------------------");
                for (Book book : books) {
                    System.out.printf("%-5d | %-20s | %-15s | %d%n",
                            book.getId(), book.getTitle(), book.getAuthor(), book.getAvailableCopies());
                }
                System.out.println("---------------------------------------------------------");
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error fetching books: " + e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            System.out.println("Error parsing book list: " + e.getMessage());
        }
    }
    
    private static void borrowBook(Scanner scanner, RestTemplate restTemplate) {
        System.out.print("Enter the ID of the book you want to borrow: ");
        long bookId = scanner.nextLong();
        scanner.nextLine(); 
        
        try {
            ResponseEntity<String> response = sendPostRequest(BOOK_SERVICE_URL + "/" + bookId + "/borrow", null, jwtToken);
            System.out.println("Book borrowed successfully! Updated book details: " + response.getBody());
        } catch (HttpClientErrorException e) {
             System.out.println("Error borrowing book: " + e.getResponseBodyAsString());
        }
    }

    private static void addBook(Scanner scanner, RestTemplate restTemplate) {
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        System.out.print("Enter available copies: ");
        int copies = scanner.nextInt();
        scanner.nextLine();

        String requestBody = String.format(
            "{\"title\":\"%s\",\"author\":\"%s\",\"availableCopies\":%d}",
            title, author, copies
        );

        try {
            ResponseEntity<String> response = sendPostRequest(BOOK_SERVICE_URL, requestBody, jwtToken);
            System.out.println("Book added successfully: " + response.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println("Error adding book: " + e.getResponseBodyAsString());
        }
    }

    private static void updateBook(Scanner scanner, RestTemplate restTemplate) {
        System.out.print("Enter the ID of the book to update: ");
        long bookId = scanner.nextLong();
        scanner.nextLine(); 

        System.out.print("Enter new title: ");
        String title = scanner.nextLine();
        System.out.print("Enter new author: ");
        String author = scanner.nextLine();
        System.out.print("Enter new available copies: ");
        int copies = scanner.nextInt();
        scanner.nextLine();

        String requestBody = String.format(
            "{\"title\":\"%s\",\"author\":\"%s\",\"availableCopies\":%d}",
            title, author, copies
        );

        try {
            sendPutRequest(BOOK_SERVICE_URL + "/" + bookId, requestBody, jwtToken);
            System.out.println("Book updated successfully!");
        } catch (HttpClientErrorException e) {
            System.out.println("Error updating book: " + e.getResponseBodyAsString());
        }
    }

    private static void deleteBook(Scanner scanner, RestTemplate restTemplate) {
        System.out.print("Enter the ID of the book to delete: ");
        long bookId = scanner.nextLong();
        scanner.nextLine();

        try {
            ResponseEntity<String> response = sendDeleteRequest(BOOK_SERVICE_URL + "/" + bookId, jwtToken);
            System.out.println(response.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println("Error deleting book: " + e.getResponseBodyAsString());
        }
    }

    private static String getRoleFromToken(String token) {
        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(payload);
            JsonNode rolesNode = jsonNode.get("roles");
            if (rolesNode != null && rolesNode.isArray() && rolesNode.size() > 0) {
                return rolesNode.get(0).asText();
            }
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing token: " + e.getMessage());
        }
        return null;
    }

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
    
    private static void sendPutRequest(String url, String body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        new RestTemplate().put(url, entity);
    }

    private static ResponseEntity<String> sendDeleteRequest(String url, String token) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return new RestTemplate().exchange(url, HttpMethod.DELETE, entity, String.class);
    }
}