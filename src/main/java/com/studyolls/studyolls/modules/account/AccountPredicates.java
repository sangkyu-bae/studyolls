package com.studyolls.studyolls.modules.account;

import com.querydsl.core.types.Predicate;
import com.studyolls.studyolls.modules.tag.Tag;
import com.studyolls.studyolls.modules.zone.Zone;

import java.util.Set;


public class AccountPredicates {

    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones){
        QAccount account=QAccount.account;
        return account.zones.any().in(zones).and(account.tags.any().in(tags));
    }

}
