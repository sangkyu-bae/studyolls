package com.studyolls.studyolls.event;

import com.studyolls.studyolls.domain.Event;
import com.studyolls.studyolls.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event,Long> {
    @EntityGraph(value = "Event.withEnrollments",type = EntityGraph.EntityGraphType.LOAD)
    List<Event> findByStudyOrderByStartDateTime(Study study);
}
