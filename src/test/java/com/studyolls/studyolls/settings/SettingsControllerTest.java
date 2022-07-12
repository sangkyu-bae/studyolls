package com.studyolls.studyolls.settings;

import com.studyolls.studyolls.modules.account.AccountRepository;
import com.studyolls.studyolls.modules.account.AccountService;
import com.studyolls.studyolls.modules.account.form.SignUpForm;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.mail.MessagingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void beforeEach() throws MessagingException {
        SignUpForm signUpForm=new SignUpForm();
        signUpForm.setNickname("keesun");
        signUpForm.setEmail("keesun@email.com");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
    }

//    @DisplayName("프로필 수정 하기 -입력값 정상")
//    @Test
//    void updateProfile()throws Exception{
//        String bio="짧은 소개를 수정하는 겨웅";
//        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
//                .param("bio",bio)
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
//                .andExpect(flash().attributeExists("message"));
//
//        Account account=accountRepository.findByNickname("test");
//        assertEquals(bio,account.getBio());
//
//    }
}