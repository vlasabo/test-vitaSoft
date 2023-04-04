package ru.vitasoft.testWork.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vitasoft.testWork.model.request.Request;


public interface RequestRepository extends JpaRepository<Request, Long> {
    Page<Request> findAllByUserIdIs(Long userId, Pageable pageable);

    Page<Request> findAllByStatusIs(Pageable pageable, String status);

    Page<Request> findAllByStatusIsAndUserIdIs(Pageable pageable, String status, Long userid);

    @EntityGraph(attributePaths = "user")
    @Query("SELECT r FROM Request r where r.id= ?1")
    Request getWithUser(Long requestId);


}
