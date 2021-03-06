package com.studyolls.studyolls.modules.study;

import com.studyolls.studyolls.modules.account.Account;
import com.studyolls.studyolls.modules.account.UserAccount;
import com.studyolls.studyolls.modules.tag.Tag;
import com.studyolls.studyolls.modules.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


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
            throw new RuntimeException("???????????? ????????? ??? ?????? ???????????????. ???????????? ?????? ??????????????? ??????????????????.");
        }
    }

    public void close() {
        if(!this.closed&&this.published){
            this.closed=true;
            this.closedDateTime=LocalDateTime.now();
        }else{
            throw  new RuntimeException("???????????? ????????? ??? ?????? ???????????????. ???????????? ???????????? ?????? ????????? ?????? ????????? ??????????????????.");

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
            throw new RuntimeException("?????? ????????? ????????? ??? ????????????. ???????????? ??????????????? ??? ?????? ??? ?????? ???????????????.");
        }
    }

    public void stopRecruit() {
        if (canUpdateRecruting()) {
            this.recruiting = false;
            this.recruitingUpdateDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("?????? ????????? ?????? ??? ????????????. ???????????? ??????????????? ??? ?????? ??? ?????? ???????????????.");
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
