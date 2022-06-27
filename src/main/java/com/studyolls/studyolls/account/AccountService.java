package com.studyolls.studyolls.account;

import com.studyolls.studyolls.config.AppProperties;
import com.studyolls.studyolls.domain.Account;
import com.studyolls.studyolls.domain.Tag;
import com.studyolls.studyolls.domain.Zone;
import com.studyolls.studyolls.mail.EmailMessage;
import com.studyolls.studyolls.mail.EmailService;
import com.studyolls.studyolls.settings.form.Notifications;
import com.studyolls.studyolls.settings.form.Profile;
import com.studyolls.studyolls.zone.ZoneForm;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final ModelMapper modelMapper;

    public Account processNewAccount(SignUpForm signUpForm) throws MessagingException {
        Account newAccount= saveNewAccount(signUpForm);
        sendSignUpCofirmEmail(newAccount);

        return newAccount;
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account=modelMapper.map(signUpForm,Account.class);
        account.generateEmailCheckToken();

        return accountRepository.save(account);
    }

    public void sendSignUpCofirmEmail(Account newAccount) throws MessagingException {
//        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
//        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage,false,"UTF-8");
//        mimeMessageHelper.setTo(newAccount.getEmail());
//        mimeMessageHelper.setSubject("스터디올레, 회원 가입 인증");
//        mimeMessageHelper.setText("/check-email-token?token="+ newAccount.getEmailCheckToken()+
//                "&email="+ newAccount.getEmail(),false);
//        javaMailSender.send(mimeMessage);
        Context context=new Context();
        context.setVariable("link","/check-email-token?token="+ newAccount.getEmailCheckToken()+
                "&email="+ newAccount.getEmail());
        context.setVariable("linkName","이메일 인증하기");
        context.setVariable("message","슽디올레 서비스를 사용하려면 링크를 클릭하세요");
        context.setVariable("host",appProperties.getHost());

        String message=templateEngine.process("mail/simple-link",context);
        EmailMessage emailMessage=EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("스터디올레, 회원 가입 인증")
                .message("/check-email-token?token="+ newAccount.getEmailCheckToken()+
                        "&email="+ newAccount.getEmail())
                .build();
        emailService.sendEmail(emailMessage);
    }


    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
       Account account= accountRepository.findByEmail(emailOrNickname);
       if(account==null){
           account= accountRepository.findByNickname(emailOrNickname);
       }

       if(account==null){
           throw new UsernameNotFoundException(emailOrNickname);
       }

        return new UserAccount(account);
    }

    public void completSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

//    public void updateProfile(Account account, Profile profile) {
//        account.setUrl(profile.getUrl());
//        account.setOccupation(profile.getOccupation());
//        account.setLocation(profile.getLocation());
//        account.setBio(profile.getBio());
//        account.setProfileImage(profile.getProfileImage());
//        accountRepository.save(account);
//    }

    public void updateProfile(Account account, Profile profile) {
        modelMapper.map(profile,account);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        modelMapper.map(notifications,account);
        accountRepository.save(account);
    }

    public void updateAccount(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);
        login(account);

    }

    public void sendLoginLink(Account account) {
        EmailMessage emailMessage=EmailMessage.builder()
                .to(account.getEmail())
                .subject("스터디올레, 회원 가입 인증")
                .message("/login-by-email?token="+ account.getEmailCheckToken()+
                        "&email="+ account.getEmail())
                .build();

        emailService.sendEmail(emailMessage);

    }

    public void addTag(Account account, Tag tag) {
        Optional<Account>byId= accountRepository.findById(account.getId());
        byId.ifPresent(a->a.getTags().add(tag));
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a->a.getTags().remove(tag));
    }

    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a->a.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a->a.getZones().remove(zone));
    }
}
