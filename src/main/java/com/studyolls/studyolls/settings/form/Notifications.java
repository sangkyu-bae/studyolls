package com.studyolls.studyolls.settings.form;

import com.studyolls.studyolls.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Notifications {
    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdateByEmail;

    private boolean studyUpdateByWeb;

}
