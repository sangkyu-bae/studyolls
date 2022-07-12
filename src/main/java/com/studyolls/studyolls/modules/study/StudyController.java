package com.studyolls.studyolls.modules.study;

import com.studyolls.studyolls.modules.account.CurrentUser;
import com.studyolls.studyolls.modules.account.Account;
import com.studyolls.studyolls.modules.study.form.StudyForm;
import com.studyolls.studyolls.modules.study.validator.StudyFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;

    private final StudyRepository studyRepository;
    private final StudyFormValidator studyFormValidator;
    @InitBinder("studyForm")
    public void studyFormInitBinder(WebDataBinder webDataBinder){
         webDataBinder.addValidators(studyFormValidator);
    }


    @GetMapping("/new-study")
    public String newStudyForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new StudyForm());


        return "study/form";
    }

    @PostMapping("/new-study")
    public String newStudySubmit(@CurrentUser Account account, Model model,@Valid StudyForm studyForm, Errors errors){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return "study/form";
        }
        Study newStudy= studyService.createNewStudy(modelMapper.map(studyForm,Study.class),account);
        return "redirect:/study/"+ URLEncoder.encode(newStudy.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/study/{path}")
    public String viewStudy(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudy(path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/view";
    }

    @GetMapping("/study/{path}/members")
    public String viewStudyMembers(@CurrentUser Account account,@PathVariable String path,Model model){
        Study study=studyService.getStudy(path);

        model.addAttribute(account);
        model.addAttribute(study);
        return "study/members";
    }
    @GetMapping("study/{path}/join")
    public String joinStudy(@CurrentUser Account account,@PathVariable String path,Model model){
        Study study=studyRepository.findStudywithMembersByPath(path);
        Set<Account> checkMember= study.getMembers();

        for(Account ac:checkMember){
            if(ac.getEmail().equals(account.getEmail())){
                model.addAttribute("message","이미 스터디에 가입된 회원입니다.");
                return "/study/"+study.getEncodedPath();
            }
        }
        studyService.addMember(study,account);

        return "redirect:/study/" + study.getEncodedPath() + "/members";
    }

    @GetMapping("study/{path}/leave")
    public String leaveStudy(@CurrentUser Account account,@PathVariable String path,Model model){
        Study study=studyRepository.findStudywithMembersByPath(path);

        Set<Account> checkMember= study.getMembers();
        for(Account ac:checkMember){
            if(!ac.getEmail().equals(account.getEmail())){
                model.addAttribute("message","이미 탈퇴된 회원입니다.");
                return "/study/"+study.getEncodedPath();
            }
        }

        studyService.removeMember(study,account);

        return "redirect:/study/" + study.getEncodedPath() + "/members";

    }
}
