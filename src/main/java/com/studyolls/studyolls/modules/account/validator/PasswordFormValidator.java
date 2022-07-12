package com.studyolls.studyolls.modules.account.validator;

import com.studyolls.studyolls.modules.account.form.PasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PasswordFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm passwordForm=(PasswordForm) target;
        if(!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())){
            errors.rejectValue("newPassword","wrong,value","입력한 패스워가 일치 하지 않습니다");
        }
    }
}
