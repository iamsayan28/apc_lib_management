package apc.library.user_service.service;

import apc.library.user_service.model.User;
import apc.library.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    // No changes needed here, the existing logic is good.
    // It throws an exception if the user is not found, which we will handle.
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(u -> {
            u.setName(userDetails.getName());
            u.setRole(userDetails.getRole());
            return userRepository.save(u);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}