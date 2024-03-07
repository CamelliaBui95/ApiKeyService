package com.routard.entities;

import com.routard.utils.Argon2;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Column(name = "EMAIL")
    private String email;

    @Column(name="CLE")
    private String cle;

    @Column(name = "QUOTA_MENSUEL")
    private int quotaMensuel;

    @Column(name="DATE_CREE")
    private LocalDate dateCreation;

    @Column(name="STATUT")
    private String statut;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clientEntity")
    private List<MailHistoryEntity> mailHistory;

    private final static Integer KEY_LENGTH = 16;

    public ClientEntity(String nomClient, String email, Integer quota) {
        ClientEntity clientEntity = new ClientEntity();
        if (!nomClient.isEmpty())
            this.nomClient = nomClient;
        if (!email.isEmpty())
            this.email = email;
        this.cle = generateKey(nomClient);
        this.quotaMensuel = quota;
        this.dateCreation = LocalDate.now();
        this.statut = "activated";
    }

    public String generateKey(String nomClient){
        String nomClientHashed = Argon2.getHashedKey(nomClient);
        System.out.println(nomClient + " : " + nomClientHashed.substring(KEY_LENGTH));
        return nomClientHashed.substring(KEY_LENGTH);
    }
}
