package com.studyolls.studyolls.modules.study.event;

import com.studyolls.studyolls.modules.study.Study;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class StudyCreatedEvent {
    private Study study;
    public StudyCreatedEvent(Study study) {
        this.study = study;
    }
}
