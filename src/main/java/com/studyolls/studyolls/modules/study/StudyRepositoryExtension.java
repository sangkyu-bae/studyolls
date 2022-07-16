package com.studyolls.studyolls.modules.study;

import com.studyolls.studyolls.modules.study.Study;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepositoryExtension {
    List<Study> findByKeyword(String keyword);
}
