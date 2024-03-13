package com.routard.entities;

import com.routard.utils.Argon2;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
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
    private int quotaMensuel; //quota = 0 quand c'est illimité.

    @Column(name="DATE_CREE")
    private LocalDateTime dateCreation;

    @Column(name="STATUT")
    private String statut;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clientEntity")
    private List<MailHistoryEntity> mailHistory;

    public static final Integer KEY_LENGH = 16;
    public static final Integer QUOTA_DEFAUT = 5;

    public ClientEntity(String nomClient, String email){
        this.nomClient=nomClient;
        this.email=email;
        this.quotaMensuel = QUOTA_DEFAUT;
        this.dateCreation = LocalDateTime.now();
        this.statut = "active";
    }
    public ClientEntity(String nomClient, String email, Integer quota) {
        if (!nomClient.isEmpty())
            this.nomClient = nomClient;
        if (!email.isEmpty())
            this.email = email;
        this.quotaMensuel = quota;
        this.dateCreation = LocalDateTime.now();
        this.statut = "active";
    }

    public String generateKey(String nomClient){
        String nomClientHashed = Argon2.getHashedKey(nomClient);
        String nomClientHashedEncoded = new String(Base64.getUrlEncoder().encode(nomClientHashed.getBytes(StandardCharsets.UTF_8)));
        String apiKey = nomClientHashedEncoded.substring(0, KEY_LENGH);
        return apiKey;
    }
}
