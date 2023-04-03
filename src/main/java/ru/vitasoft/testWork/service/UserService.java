package ru.vitasoft.testWork.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vitasoft.testWork.model.user.User;
import ru.vitasoft.testWork.repository.UserRepository;

import java.util.List;

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
}
