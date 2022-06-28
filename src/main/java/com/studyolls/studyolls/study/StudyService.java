package com.studyolls.studyolls.study;

import com.studyolls.studyolls.domain.Account;
import com.studyolls.studyolls.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyService {
    private final StudyRepository studyRepository;

    public Study createNewStudy(Study study, Account account) {
        Study newStudy=studyRepository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }
}
