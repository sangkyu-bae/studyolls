package com.studyolls.studyolls.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column
    private String email;

    @Column
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb=true;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb=true;

    private boolean studyUpdateByEmail;

    private boolean studyUpdateByWeb=true;


    public void generateEmailCheckToken() {
        this.emailCheckToken= UUID.randomUUID().toString();
    }

    public void completeSignUp() {
        this.emailVerified=true;
        this.joinedAt=LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }
}
