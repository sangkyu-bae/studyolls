package com.studyolls.studyolls.modules.study.event;

import com.studyolls.studyolls.modules.study.Study;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Async
@Component
@Transactional(readOnly = true)
public class StudyEventListener {

    @EventListener
    public void handleStudyCreateEvent(StudyCreatedEvent studyCreatedEvent){
        Study study=studyCreatedEvent.getStudy();
        log.info(study.getTitle()+"is create.");
        throw new RuntimeException();
    }
}
