package com.studyolls.studyolls.modules.event;

import com.studyolls.studyolls.modules.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {

    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);
    @EntityGraph()
    List<Enrollment> findByAccountAndAcceptedOrderByEnrolledAtDesc(Account accountLoaded, boolean accepted);
}
