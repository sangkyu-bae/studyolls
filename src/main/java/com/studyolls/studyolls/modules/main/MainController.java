package com.studyolls.studyolls.modules.main;

import com.studyolls.studyolls.modules.account.AccountRepository;
import com.studyolls.studyolls.modules.account.CurrentUser;
import com.studyolls.studyolls.modules.account.Account;
import com.studyolls.studyolls.modules.event.EnrollmentRepository;
import com.studyolls.studyolls.modules.study.Study;
import com.studyolls.studyolls.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EnrollmentRepository enrollmentRepository;
    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model){

        System.out.println(account);
        if(account!=null){
            Account accountLoaded =accountRepository.findAccountWithTagsAndZonesById(account.getId());
            model.addAttribute(accountLoaded);
            model.addAttribute("enrollmentList",enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(accountLoaded,true));
            model.addAttribute("studyManagerOf",
                    studyRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
            model.addAttribute("studyList",studyRepository.findByAccount(
                    accountLoaded.getTags(),
                    accountLoaded.getZones()
            ));
        }

        model.addAttribute("studyList",studyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true,false));

        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/search/study")
    public String searchStudy(@PageableDefault(size=9,sort = "publishedDateTime",direction = Sort.Direction.ASC)
                                  Pageable pageable, String keyword, Model model){
        Page<Study> studyPage=studyRepository.findByKeyword(keyword,pageable);
        model.addAttribute("studyPage",studyPage);
        model.addAttribute("keyword",keyword);
        model.addAttribute("sortProperty",
                pageable.getSort().toString().contains("publishedDateTime")?"publishedDateTime":"memberCount");

        return "search";
    }
}
