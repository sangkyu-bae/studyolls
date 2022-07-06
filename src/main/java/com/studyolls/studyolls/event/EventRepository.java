package com.studyolls.studyolls.event;

import com.studyolls.studyolls.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event,Long> {
}
