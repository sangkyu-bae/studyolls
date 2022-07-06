package com.studyolls.studyolls.domain;

import com.studyolls.studyolls.account.UserAccount;
import lombok.*;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(name="Study.withAll",attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")
})
@NamedEntityGraph(name="Study.withTagsAndManagers",attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("managers")
})
@NamedEntityGraph(name = "Study.withZonesAndManagers",attributeNodes = {
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers")
})
@NamedEntityGraph(name = "Study.withManagers", attributeNodes = {
        @NamedAttributeNode("managers")
})
@NamedEntityGraph(name="Study.withMembers" ,attributeNodes = {
        @NamedAttributeNode("members")
})
@Entity
@Getter@Setter@EqualsAndHashCode(of="id")
@Builder@AllArgsConstructor@NoArgsConstructor
public class Study {

    @Id @GeneratedValue
    private long id;

    @ManyToMany
    private Set<Account> managers=new HashSet<>();

    @ManyToMany
    private Set<Account> members=new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags=new HashSet<>();

    @ManyToMany
    private Set<Zone> zones=new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdateDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    private int memberCount;
    public void addManager(Account account) {
        this.managers.add(account);
    }

    public boolean isJoinable(UserAccount userAccount){
        Account account=userAccount.getAccount();
        return this.isPublished()&&this.isRecruiting()
                && !this.members.contains(account)&&!this.members.contains(account);
    }

    public boolean isMember(UserAccount userAccount){
        return this.members.contains(userAccount.getAccount());
    }
    public boolean isManager(UserAccount userAccount){
        return this.managers.contains(userAccount.getAccount());
    }

    public boolean isManagedBy(Account account) {
        return this.getManagers().contains(account);
    }

    public void publish() {
        if(!this.closed&&!this.published){
            this.published=true;
            this.publishedDateTime=LocalDateTime.now();
        }else{
            throw new RuntimeException("스터디를 공개할 수 없는 상태입니다. 스터디를 이미 공개했거나 종료했습니다.");
        }
    }

    public void close() {
        if(!this.closed&&this.published){
            this.closed=true;
            this.closedDateTime=LocalDateTime.now();
        }else{
            throw  new RuntimeException("스터디를 종료할 수 없는 상태입니다. 스터디가 공개되어 있지 않거나 이미 종료한 스터디입니다.");

        }
    }

    public boolean canUpdateRecruting() {
        return this.published && this.recruitingUpdateDateTime == null || this.recruitingUpdateDateTime.isBefore(LocalDateTime.now().minusHours(1));
    }

    public void startRecruit() {
        if(canUpdateRecruting()){
            this.recruiting=true;
            this.recruitingUpdateDateTime=LocalDateTime.now();
        }else {
            throw new RuntimeException("인원 모집을 시작할 수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요.");
        }
    }

    public void stopRecruit() {
        if (canUpdateRecruting()) {
            this.recruiting = false;
            this.recruitingUpdateDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집을 멈출 수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요.");
        }
    }

    public boolean isRemovable() {
        return !this.published;
    }

    public void addMember(Account account) {
        this.getMembers().add(account);
        this.memberCount++;
    }

    public void removeMember(Account account) {
        this.getMembers().remove(account);
        this.memberCount--;
    }

    public String getEncodedPath() {
        return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
    }
}
