package com.studyolls.studyolls.modules.study.event;

import com.studyolls.studyolls.infra.config.AppProperties;
import com.studyolls.studyolls.infra.mail.EmailMessage;
import com.studyolls.studyolls.infra.mail.EmailService;
import com.studyolls.studyolls.modules.account.Account;
import com.studyolls.studyolls.modules.account.AccountPredicates;
import com.studyolls.studyolls.modules.account.AccountRepository;
import com.studyolls.studyolls.modules.notification.Notification;
import com.studyolls.studyolls.modules.notification.NotificationRepository;
import com.studyolls.studyolls.modules.notification.NotificationType;
import com.studyolls.studyolls.modules.study.Study;
import com.studyolls.studyolls.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;
    @EventListener
    public void handleStudyCreateEvent(StudyCreatedEvent studyCreatedEvent){
        Study study=studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));

        accounts.forEach(account -> {
            if(account.isStudyCreatedByEmail()){
                senStudyCreatedEmail(study, account);
            }

            if(account.isStudyCreatedByWeb()){
                saveStudyCreatedNotification(study, account);
            }
        });

    }

    private void saveStudyCreatedNotification(Study study, Account account) {
        Notification notification=new Notification();
        notification.setTilte(study.getTitle());
        notification.setLink("/study/"+ study.getEncodedPath());
        notification.setCreateLocalDateTime(LocalDate.now());
        notification.setChecked(false);
        notification.setMessage(study.getShortDescription());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.STUDY_CREATED);
        notificationRepository.save(notification);
    }

    private void senStudyCreatedEmail(Study study, Account account) {
        Context context=new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/"+ study.getEncodedPath());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message","새로운 스터디가 생겼습니다.");
        context.setVariable("host",appProperties.getHost());
        String message= templateEngine.process("mail/simple-link",context);
        EmailMessage emailMessage=EmailMessage.builder()
                .subject("스터디올레, '"+ study.getTitle()+"' 스터디가 생겼습니다")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}
