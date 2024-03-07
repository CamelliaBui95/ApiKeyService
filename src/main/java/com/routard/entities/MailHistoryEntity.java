package com.routard.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="MAIL_HISTORY")
public class MailHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "OBJET")
    private String objetMail;

    @Column(name="DESTINATAIRE")
    private String destinataire;

    @Column(name="DATE_ENVOI")
    private LocalDate dateEnvoi;

    @Column(name = "HEURE_ENVOI")
    private LocalDate heureEnvoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ID_CLIENT")
    private ClientEntity clientEntity;
}
