package com.routard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.routard.entities.MailHistoryEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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
    private LocalDateTime dateEnvoi;
    @JsonProperty(index = 5)
    private MailHistoryDTOClient client;

    @Getter
    public class MailHistoryDTOClient {
        Integer id;
        String nom;
    }

    public MailHistoryDTO(MailHistoryEntity mailHistoryEntity){
        id = mailHistoryEntity.getId();
        objetMail = mailHistoryEntity.getObjetMail();
        destinataire = mailHistoryEntity.getDestinataire();
        dateEnvoi = mailHistoryEntity.getDateEnvoi();
        client = new MailHistoryDTOClient();
        client.id = mailHistoryEntity.getClientEntity().getId();
        client.nom = mailHistoryEntity.getClientEntity().getNomClient();
    }

    public static List<MailHistoryDTO> toDTOList(List<MailHistoryEntity> mailHistoryEntities) {
        List<MailHistoryDTO> mailHistoryDTOList = new ArrayList<>();
        for (MailHistoryEntity mailHistoryEntity : mailHistoryEntities)
            mailHistoryDTOList.add(new MailHistoryDTO(mailHistoryEntity));
        return mailHistoryDTOList;
    }
}


