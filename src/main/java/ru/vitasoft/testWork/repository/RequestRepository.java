package ru.vitasoft.testWork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vitasoft.testWork.model.request.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {

}
