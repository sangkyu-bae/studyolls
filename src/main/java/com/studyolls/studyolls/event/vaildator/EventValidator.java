package com.studyolls.studyolls.event.vaildator;

import com.studyolls.studyolls.event.form.EventForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

public class EventValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return EventValidator.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventForm eventForm=(EventForm)target;
        if(eventForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now())){
            errors.rejectValue("endEnrollmentDateTime","wrong.datetime","모임 종료 접수 일시를 정확히 입력하시요");
        }

//        if(eventForm.getEndDateTime().isBefore(eventForm.getStartDateTime())||)
    }
}
