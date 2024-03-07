package com.routard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClientToCreateDTO {
    @JsonProperty(index = 1)
    private Integer id;
    @JsonProperty(index = 2)
    private String nomClient;
    @JsonProperty(index = 3)
    private String adresseMail;
}
