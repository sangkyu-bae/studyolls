package com.studyolls.studyolls.settings.form;

import com.studyolls.studyolls.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
public class Profile {
    @Length(max=35)
    private String bio;
    @Length(max = 50)
    private String url;
    @Length(max=50)
    private String occupation;
    @Length(max=50)
    private String location;

    private String profileImage;

}
