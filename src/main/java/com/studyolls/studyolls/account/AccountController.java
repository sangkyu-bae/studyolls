package com.studyolls.studyolls.account;

import com.studyolls.studyolls.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("sginUpForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("sign-up")
    public String singUpForm(Model model){
        model.addAttribute("signUpForm",new SignUpForm());

        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) throws Exception{
        if(errors.hasErrors()){
            return "account/sign-up";
        }

        Account account= accountService.processNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model){
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";

        if(account==null){
            model.addAttribute("error","wrong email");
            return view;
        }

        if(!account.isValidToken(token)){
            model.addAttribute("error","wrong token");
            return view;
        }

        account.completeSignUp();
        accountService.login(account);
        model.addAttribute("numberOfUser",accountRepository.count());
        model.addAttribute("nickname",account.getNickname());
        return view;
    }

//    @GetMapping("recheck-email")
//    public String recheckEmail(String email ,Model model){
//        Account account=accountRepository.findByEmail(email);
//        String view="account/recheck-email";
//        if(account ==null){
//            model.addAttribute("error","가입되지 않은 이메일입니다.");
//            return view;
//        }
//
//        model.addAttribute(account);
//        account.generateEmailCheckToken();
//        accountService.sendSignUpCofirmEmail(account);
//
//        return view;
//    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account account,Model model){

        model.addAttribute("email",account.getEmail());

        return "account/check-email";
    }

    @PostMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account account,Model Model){
        accountService.sendSignUpCofirmEmail(account);
        return "redirect:/";
    }


}
