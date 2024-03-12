package com.routard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.routard.entities.MailHistoryEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MailHistoryDTO {
    @JsonProperty(index = 1)
    private Integer id;
    @JsonProperty(index = 2)
    private String objetMail;
    @JsonProperty(index = 3)
    private String destinataire;
    @JsonProperty(index = 4)
    private LocalDate dateEnvoi;
    @JsonProperty(index = 5)
    private LocalTime heureEnvoi;
    @JsonProperty(index = 6)
    private MailHistoryDTOClient client;
//    private ClientDTO clientDTO;

    @Getter
    class MailHistoryDTOClient {
        Integer id;
        String nom;
    }

    public MailHistoryDTO(MailHistoryEntity mailHistoryEntity){
        id = mailHistoryEntity.getId();
        objetMail = mailHistoryEntity.getObjetMail();
        destinataire = mailHistoryEntity.getDestinataire();
        dateEnvoi = mailHistoryEntity.getDateEnvoi();
        heureEnvoi = mailHistoryEntity.getHeureEnvoi();
        client = new MailHistoryDTOClient();
        client.id = mailHistoryEntity.getClientEntity().getId();
        client.nom = mailHistoryEntity.getClientEntity().getNomClient();
//        clientDTO = new ClientDTO(mailHistoryEntity.getClientEntity());
    }

    public static List<MailHistoryDTO> toDTOList(List<MailHistoryEntity> mailHistoryEntities) {
        List<MailHistoryDTO> mailHistoryDTOList = new ArrayList<>();
        for (MailHistoryEntity mailHistoryEntity : mailHistoryEntities)
            mailHistoryDTOList.add(new MailHistoryDTO(mailHistoryEntity));
        return mailHistoryDTOList;
    }
}


