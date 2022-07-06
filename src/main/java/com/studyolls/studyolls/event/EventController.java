package com.studyolls.studyolls.event;

import com.studyolls.studyolls.account.CurrentUser;
import com.studyolls.studyolls.domain.Account;
import com.studyolls.studyolls.domain.Event;
import com.studyolls.studyolls.domain.Study;
import com.studyolls.studyolls.event.form.EventForm;
import com.studyolls.studyolls.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {

    private final StudyService studyService;
    private final EventService eventService;

    private final ModelMapper modelMapper;


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

}
