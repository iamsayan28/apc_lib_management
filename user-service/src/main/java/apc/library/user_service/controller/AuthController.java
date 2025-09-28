// package apc.library.user_service.controller;

// import apc.library.user_service.config.JwtUtil;
// import apc.library.user_service.dto.AuthRequest;
// import apc.library.user_service.model.User;
// import apc.library.user_service.repository.UserRepository;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/auth")
// public class AuthController {

//     @Autowired private AuthenticationManager authenticationManager;
//     @Autowired private UserRepository userRepository;
//     @Autowired private PasswordEncoder passwordEncoder;
//     @Autowired private JwtUtil jwtUtil;

//     @PostMapping("/register")
//     public ResponseEntity<String> registerUser(@RequestBody User user) {
//         // Check if the email is already taken
//         if (userRepository.findByEmail(user.getEmail()).isPresent()) {
//             return ResponseEntity
//                     .badRequest()
//                     .body("Error: Email is already in use!");
//         }

//         user.setPassword(passwordEncoder.encode(user.getPassword()));
//         // By default, you might want to set a new user's role to "ROLE_USER"
//         if (user.getRole() == null || user.getRole().isEmpty()) {
//             user.setRole("ROLE_USER");
//         }
        
//         userRepository.save(user);
//         return ResponseEntity.ok("User registered successfully");
//     }

//     @PostMapping("/login")
//     public ResponseEntity<String> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
//         authenticationManager.authenticate(
//             new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
//         );

//         final UserDetails userDetails = userRepository.findByEmail(authRequest.getEmail())
//             .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authRequest.getEmail()));

//         final String jwt = jwtUtil.generateToken(userDetails);

//         return ResponseEntity.ok(jwt);
//     }
// }

package apc.library.user_service.controller;

import apc.library.user_service.config.JwtUtil;
import apc.library.user_service.dto.AuthRequest;
import apc.library.user_service.model.User;
import apc.library.user_service.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            System.out.println("=== REGISTRATION DEBUG ===");
            System.out.println("Registration attempt for email: " + user.getEmail());
            System.out.println("Name: " + user.getName());
            System.out.println("Role: " + user.getRole());
            
            // Check if the email is already taken
            Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                System.out.println("Email already exists: " + user.getEmail());
                return ResponseEntity
                        .badRequest()
                        .body("Error: Email is already in use!");
            }

            // Encode password
            String originalPassword = user.getPassword();
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            
            System.out.println("Original password: " + originalPassword);
            System.out.println("Encoded password: " + encodedPassword);
            
            // Set default role if not provided
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("ROLE_USER");
            }
            
            // Save user
            User savedUser = userRepository.save(user);
            System.out.println("User saved with ID: " + savedUser.getId());
            
            // Verify user was saved by trying to find it
            Optional<User> verifyUser = userRepository.findByEmail(user.getEmail());
            if (verifyUser.isPresent()) {
                System.out.println("Verification: User found in database after save");
                System.out.println("Saved user email: " + verifyUser.get().getEmail());
                System.out.println("Saved user ID: " + verifyUser.get().getId());
            } else {
                System.out.println("ERROR: User NOT found in database after save!");
            }
            
            // Show all users in database for debugging
            List<User> allUsers = userRepository.findAll();
            System.out.println("Total users in database: " + allUsers.size());
            for (User u : allUsers) {
                System.out.println("User: ID=" + u.getId() + ", Email=" + u.getEmail() + ", Role=" + u.getRole());
            }
            
            System.out.println("=== END REGISTRATION DEBUG ===");
            return ResponseEntity.ok("User registered successfully");
            
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        try {
            System.out.println("=== LOGIN DEBUG ===");
            System.out.println("Raw AuthRequest object: " + authRequest);
            System.out.println("AuthRequest.getEmail(): " + authRequest.getEmail());
            System.out.println("AuthRequest.getPassword(): " + authRequest.getPassword());
            
            if (authRequest.getEmail() == null) {
                System.err.println("ERROR: Email is null in AuthRequest!");
                return ResponseEntity.badRequest().body("Email cannot be null");
            }
            
            System.out.println("Login attempt for email: " + authRequest.getEmail());
            System.out.println("Password provided: " + authRequest.getPassword());
            
            // Show all users in database for debugging
            List<User> allUsers = userRepository.findAll();
            System.out.println("Total users in database during login: " + allUsers.size());
            for (User u : allUsers) {
                System.out.println("Available User: ID=" + u.getId() + ", Email=" + u.getEmail() + ", Role=" + u.getRole());
            }
            
            // Try to find user
            Optional<User> userOptional = userRepository.findByEmail(authRequest.getEmail());
            if (!userOptional.isPresent()) {
                System.err.println("ERROR: User not found with email: " + authRequest.getEmail());
                System.out.println("=== END LOGIN DEBUG ===");
                return ResponseEntity.badRequest().body("User not found with email: " + authRequest.getEmail());
            }
            
            User user = userOptional.get();
            System.out.println("User found: " + user.getEmail() + ", Role: " + user.getRole());
            System.out.println("Stored password hash: " + user.getPassword());
            
            // Test password matching manually
            boolean passwordMatches = passwordEncoder.matches(authRequest.getPassword(), user.getPassword());
            System.out.println("Password matches: " + passwordMatches);
            
            // Try to authenticate
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
            
            System.out.println("Authentication successful for: " + authRequest.getEmail());
            
            // Generate JWT token
            final String jwt = jwtUtil.generateToken(user);
            System.out.println("JWT token generated successfully");
            System.out.println("=== END LOGIN DEBUG ===");
            
            return ResponseEntity.ok(jwt);
            
        } catch (BadCredentialsException e) {
            System.err.println("Bad credentials for email: " + authRequest.getEmail());
            System.out.println("=== END LOGIN DEBUG ===");
            return ResponseEntity.badRequest().body("Invalid email or password");
        } catch (UsernameNotFoundException e) {
            System.err.println("User not found: " + authRequest.getEmail());
            System.out.println("=== END LOGIN DEBUG ===");
            return ResponseEntity.badRequest().body("User not found");
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== END LOGIN DEBUG ===");
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }
}