package com.studyolls.studyolls.domain;

import lombok.*;

import javax.persistence.*;
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

    public void addManager(Account account) {
        this.managers.add(account);
    }
}
