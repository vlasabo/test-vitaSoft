package ru.vitasoft.testWork.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.vitasoft.testWork.model.request.Request;


public interface RequestRepository extends JpaRepository<Request, Long> {
    Page<Request> findAllByUserIdIs(Long userId, Pageable pageable);
}
