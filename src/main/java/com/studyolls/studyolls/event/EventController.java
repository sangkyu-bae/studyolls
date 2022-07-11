package com.studyolls.studyolls.event;

import com.studyolls.studyolls.account.CurrentUser;
import com.studyolls.studyolls.domain.Account;
import com.studyolls.studyolls.domain.Event;
import com.studyolls.studyolls.domain.EventType;
import com.studyolls.studyolls.domain.Study;
import com.studyolls.studyolls.event.form.EventForm;
import com.studyolls.studyolls.event.vaildator.EventValidator;
import com.studyolls.studyolls.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {

    private final StudyService studyService;
    private final EventService eventService;

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentUser Account account, @PathVariable String path, Model model){
        Study study=studyService.getStudyToupdateStatus(account,path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(new EventForm());

        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentUser Account account, @PathVariable String path,
                                 @Valid EventForm eventForm, Errors errors, Model model){

        Study study=studyService.getStudyToupdateStatus(account,path);
        if(errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/form";
        }

        Event event=eventService.createEvent(modelMapper.map(eventForm, Event.class),study,account);
        return "redirect:/study/"+study.getEncodedPath()+"/events/"+event.getId();
    }

    @GetMapping("/events/{id}")
    public String getEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id,
                           Model model){
        model.addAttribute(account);
        model.addAttribute(eventRepository.findById(id).orElseThrow());
        model.addAttribute(studyService.getStudy(path));

        return "event/view";
    }

    @GetMapping("/events")
    public String viewStudyEvent(@CurrentUser Account account,@PathVariable String path,
                                 Model model){
        Study study=studyService.getStudy(path);
        model.addAttribute(study);
        model.addAttribute(account);

        List<Event> events=eventRepository.findByStudyOrderByStartDateTime(study);
        List<Event> newEvents=new ArrayList<>();
        List<Event> oldEvents=new ArrayList<>();

        events.forEach(e->{
            if(e.getEndDateTime().isBefore(LocalDateTime.now())){
                oldEvents.add(e);
            }else{
                newEvents.add(e);
            }
        });

        model.addAttribute("newEvents",newEvents);
        model.addAttribute("oldEvents",oldEvents);

        return "study/events";
    }

    @GetMapping("/events/{id}/edit")
    public String updateEventForm(@CurrentUser Account account,@PathVariable String path,
                                  @PathVariable Long id,Model model){
        Study study=studyService.getStudyToUpdate(account,path);
        Event event=eventRepository.findById(id).orElseThrow();
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event,EventForm.class));
        return "event/update-form";
    }

    @PostMapping("/events/{id}/edit")
    public String updateEventSubmit(@CurrentUser Account account,@PathVariable String path,
                                    @PathVariable Long id, Model model,@Valid EventForm eventForm,
                                    Errors errors){
        Study study=studyService.getStudyToUpdate(account,path);
        Event event=eventRepository.findById(id).orElseThrow();
        eventForm.setEventType(event.getEventType());
        eventValidator.validateUpdateForm(eventForm,event,errors);


        if(errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute(event);
            return "event/update-form";
        }

        eventService.updateEvent(event,eventForm);

        return "redirect:/study/"+study.getEncodedPath()+"/events/"+event.getId();
    }

    @DeleteMapping("/events/{id}")
    public String cancelEvent(@CurrentUser Account account, @PathVariable String path,@PathVariable Long id){
        Study study=studyService.getStudyToupdateStatus(account,path);
        eventService.deleteEvent(eventRepository.findById(id).orElseThrow());

        return "redirect:/study/"+study.getEncodedPath()+"/events";
    }

    @PostMapping("/events/{id}/enroll")
    public String newEnroll(@CurrentUser Account account,@PathVariable String path,@PathVariable Long id){
        Study study=studyService.getStudyToEnroll(path);
        eventService.newEnrollmet(eventRepository.findById(id).orElseThrow(),account);
        return "redirect:/study/"+study.getEncodedPath()+"/events/"+id;
    }

    @PostMapping("/events/{id}/disenroll")
    public String cancelEnrollment(@CurrentUser Account account,@PathVariable String path,@PathVariable Long id){
        Study study=studyService.getStudyToEnroll(path);
        eventService.cancelEnrollment(eventRepository.findById(id).orElseThrow(),account);
        return "redirect:/study/"+study.getEncodedPath()+"/events/"+id;

    }
}
