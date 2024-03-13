package com.routard.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="MAIL_HISTORY")
public class MailHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MAIL")
    private Integer id;

    @Column(name = "OBJET")
    private String objetMail;

    @Column(name="DESTINATAIRE")
    private String destinataire;

    @Column(name="DATE_ENVOI")
    private LocalDateTime dateEnvoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ID_CLIENT")
    private ClientEntity clientEntity;
}
