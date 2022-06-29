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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
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

        accountService.completSignUp(account);

        model.addAttribute("numberOfUser",accountRepository.count());
        model.addAttribute("nickname",account.getNickname());
        return view;
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account account,Model model){

        model.addAttribute("email",account.getEmail());

        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account account,Model Model) throws MessagingException {
        accountService.sendSignUpCofirmEmail(account);
        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname,Model model,@CurrentUser Account account){

        Account accountToView=accountService.getAccount(nickname);

        model.addAttribute(accountToView);
        model.addAttribute("isOwner",accountToView.equals(account));
        return "account/profile";
    }

    @GetMapping("/email-login")
    public String emailLoginForm(){
        return "account/email-login";
    }
    @PostMapping("email-login")
    public String sendEmailLoginLink(String email, Model model, RedirectAttributes attributes){

       Account account=accountRepository.findByEmail(email);
       if(account==null){
           model.addAttribute("error","유효한 이메일이 없습니다.");
           return "account/email-login";
       }

       accountService.sendLoginLink(account);
        attributes.addFlashAttribute("message","이메일 인증 메일을 발송 했습니다.");

        return "redirect:/email-login";
    }
    @GetMapping("/login-by-email")
    public String loginByEmail(String token,String email,Model model){
        Account account=accountRepository.findByEmail(email);
        String view="account/logged-in-by-email";

        if(account==null||!account.isValidToken(token)){
            model.addAttribute("error", "로그인할 수 없습니다.");
            return view;
        }

        accountService.login(account);
        return view;
    }

//    @GetMapping("profile/{nickname}")
//    public String viewProfile(@PathVariable String nickname, Model model, @CurrentUser Account account) {
//        Account byNickname= accountRepository.findByNickname(nickname);
//        if(nickname==null){
//            throw new IllegalArgumentException(nickname+"에 해당하는 사용자가 없습니다");
//        }
//        model.addAttribute(byNickname);
//        model.addAttribute("isOwner", byNickname.equals(account));
//        return "account/profile";
//    }

//    @GetMapping("/profile")
//    public String viewProfile() {
//
//        return "account/profile";
//    }

    @GetMapping("/test")
    public String tests(){
        System.out.println("여길안타?");
        return "account/test";
    }


}
