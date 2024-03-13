package com.routard.dto;

import com.routard.utils.Validator;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailClient {
    private String recipient;
    private String subject;
    private String content;

    public boolean isValid(){
        return Validator.isValidEmailAddress(recipient);
    }
}
