package com.studyolls.studyolls.modules.study;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study,Long>,StudyRepositoryExtension {
    boolean existsByPath(String path);

    @EntityGraph(attributePaths={"tags","zones","managers","members",})
    Study findByPath(String path);
    @EntityGraph(attributePaths={"tags","managers"})
    Study findStudyWithTagsByPath(String path);

    @EntityGraph(attributePaths={"zones","managers"})
    Study findStudyWithZonesByPath(String path);
    @EntityGraph(attributePaths="managers")
    Study findStudyWithManagerByPath(String path);

    @EntityGraph(attributePaths="members")
    Study findStudywithMembersByPath(String path);

    Study findStudyOnlyByPath(String path);
    @EntityGraph(attributePaths={"zones","tags"})
    Study findStudyWithTagsAndZonesById(long id);

    @EntityGraph(attributePaths = {"members","managers"})
    Study findStudyWithManagersAndMembersById(long id);
}
