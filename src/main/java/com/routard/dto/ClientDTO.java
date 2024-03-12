package com.routard.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.routard.entities.ClientEntity;
import com.routard.entities.MailHistoryEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ClientDTO {
    @JsonProperty(index = 1)
    private Integer id;
    @JsonProperty(index = 2)
    private String nomClient;
    @JsonProperty(index = 3)
    @JsonIgnore
    private String email;
    @JsonProperty(index = 4)
    private String cle;
    @JsonProperty(index = 5)
    private int quotaMensuel;
    @JsonProperty(index = 6)
    @JsonIgnore
    private LocalDateTime dateCreation;
    @JsonProperty(index = 7)
    private String statut;
    @JsonProperty(index = 8)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ClientDTOMail> mails;

    @Getter
    class ClientDTOMail {
        Integer id;
        LocalDate dateEnvoi;
        LocalTime heureEnvoi;
        String destinataire;
        String objetMail;
    }

    public ClientDTO(ClientEntity clientEntity) {
        id = clientEntity.getId();
        nomClient = clientEntity.getNomClient();
        email = clientEntity.getEmail();
        cle = clientEntity.getCle();
        quotaMensuel = clientEntity.getQuotaMensuel();
        dateCreation = clientEntity.getDateCreation();
        statut = clientEntity.getStatut();
        mails = new ArrayList<>();
        if (clientEntity.getMailHistory() != null) {
            for (MailHistoryEntity mail : clientEntity.getMailHistory()) {
                ClientDTOMail clientDTOMail = new ClientDTOMail();
                clientDTOMail.id = mail.getId();
                clientDTOMail.dateEnvoi = mail.getDateEnvoi();
                clientDTOMail.heureEnvoi = mail.getHeureEnvoi();
                clientDTOMail.destinataire = mail.getDestinataire();
                clientDTOMail.objetMail = mail.getObjetMail();
                mails.add(clientDTOMail);
            }
        }
    }

    public static List<ClientDTO> toDTOList(List<ClientEntity> clientEntities) {
        List<ClientDTO> clientDTOList = new ArrayList<>();
        for (ClientEntity clientEntity: clientEntities)
            clientDTOList.add(new ClientDTO(clientEntity));
        return clientDTOList;
    }
}
