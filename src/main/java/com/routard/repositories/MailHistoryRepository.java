package com.routard.repositories;

import com.routard.entities.ClientEntity;
import com.routard.entities.MailHistoryEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.RequestScoped;

import java.time.LocalDate;

@RequestScoped
public class MailHistoryRepository implements PanacheRepositoryBase<MailHistoryEntity, Integer> {
    public Integer getMailCount(ClientEntity clientEntity) {
        if (clientEntity==null) return null;
        LocalDate date = LocalDate.now();
        Integer month = date.getMonthValue();
        Integer year = date.getYear();
        Long mailcount = count("clientEntity = ?1 and YEAR(dateEnvoi)=?2 and MONTH(dateEnvoi) = ?3", clientEntity, year, month );
        return mailcount.intValue();
    }
}
