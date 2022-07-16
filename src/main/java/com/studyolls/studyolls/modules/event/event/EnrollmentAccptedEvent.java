package com.studyolls.studyolls.modules.event.event;

import com.studyolls.studyolls.modules.event.Enrollment;

public class EnrollmentAccptedEvent extends EnrollmentEvent{

    public EnrollmentAccptedEvent(Enrollment enrollment) {
        super(enrollment, "모임 참가 신청을 확인했습니다. 모임에 참석하세요.");
    }
}
