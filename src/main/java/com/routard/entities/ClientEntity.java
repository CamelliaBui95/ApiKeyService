package com.routard.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="CLIENT")
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_CLIENT")
    private Integer id;

    @Column(name = "NOM_CLIENT")
    private String nomClient;

    @Column(name="CLE")
    private String cle;

    @Column(name = "QUOTA_MENSUEL")
    private int quotaMensuel;

    @Column(name="DATE_CREE")
    private LocalDate dateCree;

    @Column(name="STATUT")
    private String statut;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    private List<MailHistoryEntity> mailHistory;
}
