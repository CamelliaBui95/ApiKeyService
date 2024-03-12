package com.routard.dto;

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
}
