package com.studyolls.studyolls.domain;

import com.studyolls.studyolls.account.UserAccount;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NamedEntityGraph(
        name="Event.withEnrollments",
        attributeNodes = @NamedAttributeNode("enrollments")
)
@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
public class Event {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study;

    @ManyToOne
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column
    private int limitOfEnrollments;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "event")
    private List<Enrollment>enrollments;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public boolean isEnrollableFor(UserAccount userAccount){
        return isNotClosed()&& !isAttended(userAccount)&&!isAlreadyEnrolled(userAccount);
    }
    public boolean isDisenrollableFor(UserAccount userAccount) {
        return isNotClosed() && !isAttended(userAccount) && isAlreadyEnrolled(userAccount);
    }
    private boolean isAlreadyEnrolled(UserAccount userAccount) {
        Account account=userAccount.getAccount();

        for(Enrollment e :this.enrollments){
            if(e.getAccount().equals(account)){
                return true;
            }
        }

        return false;
    }

    public boolean isAttended(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().equals(account) && e.isAttended()) {
                return true;
            }
        }

        return false;
    }

    public int numberOfRemainSpots(){
        return this.limitOfEnrollments -(int) this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }
    private boolean isNotClosed() {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }


    public long getNumberOfAcceptedEnrollments() {
        return this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setEvent(this);
    }

    public boolean isAbleToAccptWaitingEnrollment() {
        return this.eventType==EventType.FCFS&&this.limitOfEnrollments>this.getNumberOfAcceptedEnrollments();
    }

    public boolean canAccept(Enrollment enrollment){
        return this.eventType==EventType.CONFIRMATIVE
                &&this.enrollments.contains(enrollment)
                &&!enrollment.isAttended()
                &&!enrollment.isAccepted();
    }

    public boolean canReject(Enrollment enrollment){
        return this.eventType==EventType.CONFIRMATIVE
                &&this.enrollments.contains(enrollment)
                &&!enrollment.isAttended()
                &&enrollment.isAccepted();
    }

    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
        enrollment.setEvent(null);
    }

    private Enrollment getTheFirstWaitingEnrollment() {
        for(Enrollment e:this.enrollments){
            if(!e.isAccepted()){
                return e;
            }
        }
        return null;
    }

    public void acceptNextWaitingEnrollment() {
        if(this.isAbleToAccptWaitingEnrollment()){
            Enrollment enrollmentToAccpt=this.getTheFirstWaitingEnrollment();
            if(enrollmentToAccpt!=null){
                enrollmentToAccpt.setAccepted(true);
            }
        }
    }

    public void accptWaitingList() {
        if(this.isAbleToAccptWaitingEnrollment()){
            List<Enrollment> watingList=getWatingList();
            int numberToAccpte=(int) Math.min(this.limitOfEnrollments - this.getNumberOfAcceptedEnrollments(),watingList.size());
            watingList.subList(0,numberToAccpte).forEach(e->e.setAccepted(true));
        }
    }

    private List<Enrollment> getWatingList() {
      return this.enrollments.stream().filter(enrollment -> !enrollment.isAccepted()).collect(Collectors.toList());
    }
}
