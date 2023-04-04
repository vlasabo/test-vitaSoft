package ru.vitasoft.testWork.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vitasoft.testWork.model.security.Role;
import ru.vitasoft.testWork.model.user.User;
import ru.vitasoft.testWork.repository.UserRepository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public List<User> findUserByPartOfUsername(String username) {
        return userRepository.findAllByUsernameContainingIgnoreCase(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User setRoleToUser(Long userId, Role role) {
        User user = userRepository.findById(userId).orElseThrow();
        Set<Role> roles = user.getRoles();
        roles.add(role);
        return userRepository.save(user);
    }
}
