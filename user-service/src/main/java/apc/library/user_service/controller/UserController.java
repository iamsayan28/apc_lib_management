// package apc.library.user_service.controller;

// import apc.library.user_service.model.User;
// import apc.library.user_service.repository.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/users")
// public class UserController {

//     @Autowired
//     private UserRepository userRepository;

//     @PostMapping
//     public User createUser(@RequestBody User user) {
//         return userRepository.save(user);
//     }

//     @GetMapping
//     public List<User> getAllUsers() {
//         return userRepository.findAll();
//     }

//     @GetMapping("/{id}")
//     public User getUserById(@PathVariable Long id) {
//         return userRepository.findById(id).orElse(null);
//     }

//     @PutMapping("/{id}")
//     public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
//         User user = userRepository.findById(id).orElse(null);
//         if (user != null) {
//             user.setName(userDetails.getName());
//             user.setEmail(userDetails.getEmail());
//             return userRepository.save(user);
//         }
//         return null;
//     }

//     @DeleteMapping("/{id}")
//     public String deleteUser(@PathVariable Long id) {
//         userRepository.deleteById(id);
//         return "User deleted";
//     }
// }


package apc.library.user_service.controller;

import apc.library.user_service.model.User;
import apc.library.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Add this debug endpoint (temporary)
    @GetMapping("/debug/all")
    public ResponseEntity<List<User>> getAllUsersDebug() {
        List<User> users = userRepository.findAll();
        System.out.println("DEBUG: Total users in database: " + users.size());
        for (User u : users) {
            System.out.println("DEBUG User: ID=" + u.getId() + ", Email=" + u.getEmail() + 
                             ", Name=" + u.getName() + ", Role=" + u.getRole());
        }
        return ResponseEntity.ok(users);
    }

    // Add this debug endpoint to search by email
    @GetMapping("/debug/email/{email}")
    public ResponseEntity<String> findUserByEmail(@PathVariable String email) {
        System.out.println("DEBUG: Searching for user with email: " + email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User foundUser = user.get();
            String result = "User found: ID=" + foundUser.getId() + 
                           ", Email=" + foundUser.getEmail() + 
                           ", Name=" + foundUser.getName() + 
                           ", Role=" + foundUser.getRole();
            System.out.println("DEBUG: " + result);
            return ResponseEntity.ok(result);
        } else {
            System.out.println("DEBUG: User not found with email: " + email);
            return ResponseEntity.ok("User not found with email: " + email);
        }
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            return userRepository.save(user);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "User deleted";
    }
}