package com.studyolls.studyolls.modules.notification;

import com.studyolls.studyolls.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
public class Notification {

    @Id @GeneratedValue
    private Long id;

    private String tilte;

    private String link;

    private String message;

    private boolean checked;

    @ManyToOne
    private Account account;

    private LocalDate createLocalDateTime;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

}
