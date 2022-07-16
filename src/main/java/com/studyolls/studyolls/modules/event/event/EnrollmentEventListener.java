package com.studyolls.studyolls.modules.event.event;

import com.studyolls.studyolls.infra.config.AppProperties;
import com.studyolls.studyolls.infra.mail.EmailMessage;
import com.studyolls.studyolls.infra.mail.EmailService;
import com.studyolls.studyolls.modules.account.Account;
import com.studyolls.studyolls.modules.event.Enrollment;
import com.studyolls.studyolls.modules.event.Event;
import com.studyolls.studyolls.modules.notification.Notification;
import com.studyolls.studyolls.modules.notification.NotificationRepository;
import com.studyolls.studyolls.modules.notification.NotificationType;
import com.studyolls.studyolls.modules.study.Study;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class EnrollmentEventListener {
    private final NotificationRepository notificationRepository;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    @EventListener
    public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent){
        Enrollment enrollment=enrollmentEvent.getEnrollment();
        Account account=enrollment.getAccount();
        Event event=enrollment.getEvent();
        Study study=event.getStudy();

        if(account.isStudyEnrollmentResultByEmail()){
            sendEmail(enrollmentEvent,account,event,study);
        }

        if(account.isStudyEnrollmentResultByWeb()){
            createNotification(enrollmentEvent,account, event,study);
        }

    }
    private void sendEmail(EnrollmentEvent enrollmentEvent, Account account, Event event, Study study) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.getEncodedPath() + "/events/" + event.getId());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("스터디올래, " + event.getTitle() + " 모임 참가 신청 결과입니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(EnrollmentEvent enrollmentEvent, Account account, Event event, Study study) {
        Notification notification = new Notification();
        notification.setTitle(study.getTitle() + " / " + event.getTitle());
        notification.setLink("/study/" + study.getEncodedPath() + "/events/" + event.getId());
        notification.setChecked(false);
        notification.setCreateLocalDateTime(LocalDate.now());
        notification.setMessage(enrollmentEvent.getMessage());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.EVENT_ENROLLMENT);
        notificationRepository.save(notification);
    }


}