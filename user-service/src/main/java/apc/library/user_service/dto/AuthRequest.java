package apc.library.user_service.dto;

public class AuthRequest {
    
    private String email;
    private String password;

    // Default constructor (REQUIRED for Jackson)
    public AuthRequest() {
    }

    // Constructor with parameters
    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // IMPORTANT: Getters and setters must match the JSON field names exactly
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AuthRequest{" +
                "email='" + email + '\'' +
                ", password='" + (password != null ? "[HIDDEN]" : "null") + '\'' +
                '}';
    }
}