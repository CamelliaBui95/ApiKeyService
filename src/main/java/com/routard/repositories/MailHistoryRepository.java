package com.routard.repositories;

import com.routard.entities.MailHistoryEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import java.time.LocalDate;

public class MailHistoryRepository implements PanacheRepositoryBase<MailHistoryEntity, Integer> {
    public Long getMailCount(Integer id) {
        if (id==null) return null;
        LocalDate date = LocalDate.now();
        return this.count("clientEntity = ?1 and MONTH(dateEnvoi) = ?2", id, date.getMonth() );
    }
}
