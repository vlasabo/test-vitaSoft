package ru.vitasoft.testWork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vitasoft.testWork.model.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
