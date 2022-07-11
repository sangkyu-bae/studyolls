package com.studyolls.studyolls.event;

import com.studyolls.studyolls.domain.Account;
import com.studyolls.studyolls.domain.Enrollment;
import com.studyolls.studyolls.domain.Event;
import com.studyolls.studyolls.domain.Study;
import com.studyolls.studyolls.event.form.EventForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EnrollmentRepository enrollmentRepository;
    public Event createEvent(Event event, Study study, Account account) {
        event.setCreatedBy(account);
        event.setCreateDateTime(LocalDateTime.now());
        event.setStudy(study);
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm,event);
        event.accptWaitingList();
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
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
        event.removeEnrollment(enrollment);
        enrollmentRepository.delete(enrollment);
        event.acceptNextWaitingEnrollment();
    }
}
