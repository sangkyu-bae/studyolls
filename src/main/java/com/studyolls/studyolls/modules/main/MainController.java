package com.studyolls.studyolls.modules.main;

import com.studyolls.studyolls.modules.account.CurrentUser;
import com.studyolls.studyolls.modules.account.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
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
}
