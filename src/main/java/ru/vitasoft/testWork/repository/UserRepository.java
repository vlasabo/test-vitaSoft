package ru.vitasoft.testWork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vitasoft.testWork.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByUsername(String username);

        List<User> findAllByUsernameContainingIgnoreCase(String username);
}
