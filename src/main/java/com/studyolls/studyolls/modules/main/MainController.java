package com.studyolls.studyolls.modules.main;

import com.studyolls.studyolls.modules.account.CurrentUser;
import com.studyolls.studyolls.modules.account.Account;
import com.studyolls.studyolls.modules.study.Study;
import com.studyolls.studyolls.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final StudyRepository studyRepository;
    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model){

        System.out.println(account);
        if(account!=null){
            model.addAttribute(account);
        }

        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/search/study")
    public String searchStudy(String keyword,Model model){
        List<Study> studyList=studyRepository.findByKeyword(keyword);
        model.addAttribute(studyList);
        model.addAttribute("keyword",keyword);

        return "search";
    }
}
