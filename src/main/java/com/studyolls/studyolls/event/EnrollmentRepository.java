package com.studyolls.studyolls.event;

import com.studyolls.studyolls.domain.Account;
import com.studyolls.studyolls.domain.Enrollment;
import com.studyolls.studyolls.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {

    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);
}
