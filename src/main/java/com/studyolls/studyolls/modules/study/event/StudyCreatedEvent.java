package com.studyolls.studyolls.modules.study.event;

import com.studyolls.studyolls.modules.study.Study;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Getter
@RequiredArgsConstructor
public class StudyCreatedEvent {
    private final Study study;

}
