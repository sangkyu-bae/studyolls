package com.studyolls.studyolls.modules.event;

import com.studyolls.studyolls.modules.account.Account;
import com.studyolls.studyolls.modules.event.event.EnrollmentAccptedEvent;
import com.studyolls.studyolls.modules.event.event.EnrollmentRejectedEvent;
import com.studyolls.studyolls.modules.study.Study;
import com.studyolls.studyolls.modules.event.form.EventForm;
import com.studyolls.studyolls.modules.study.event.StudyUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventPublicationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final ApplicationEventPublisher eventPublisher;

    private final EnrollmentRepository enrollmentRepository;
    public Event createEvent(Event event, Study study, Account account) {
        event.setCreatedBy(account);
        event.setCreateDateTime(LocalDateTime.now());
        event.setStudy(study);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
                "'"+event.getTitle()+"' 모임을 만들었습니다."));
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm,event);
        event.accptWaitingList();
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
                "'"+event.getTitle()+"' 모임 정보를 수정했으니 확인하세요."));
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
                "'"+event.getTitle()+"' 모임을 취소했습니다."));
    }

    public void newEnrollmet(Event event, Account account) {
       if(!enrollmentRepository.existsByEventAndAccount(event,account)){
           Enrollment enrollment=new Enrollment();
           enrollment.setEnrolledAt(LocalDateTime.now());
           enrollment.setAccepted(event.isAbleToAccptWaitingEnrollment());
           enrollment.setAccount(account);
           event.addEnrollment(enrollment);
           enrollmentRepository.save(enrollment);
       }
    }

    public void cancelEnrollment(Event event, Account account) {
        Enrollment enrollment=enrollmentRepository.findByEventAndAccount(event,account);
        if(!enrollment.isAttended()) {
            event.removeEnrollment(enrollment);
            enrollmentRepository.delete(enrollment);
            event.acceptNextWaitingEnrollment();
        }
    }

    public void acceptEnrollment(Event event, Enrollment enrollment) {
        event.accept(enrollment);
        eventPublisher.publishEvent(new EnrollmentAccptedEvent(enrollment));
    }

    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.reject(enrollment);
        eventPublisher.publishEvent(new EnrollmentRejectedEvent(enrollment));
    }
    public void checkInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(true);
    }

    public void cancelCheckInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(false);
    }


}
