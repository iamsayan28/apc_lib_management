package apc.library.user_service.dto;

public class AuthRequest {
    // Use 'email' because the client sends an email during login
    private String email;
    private String password;

    // Getters and setters are required for the JSON to be processed correctly
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
}