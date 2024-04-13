package com.shj.testapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequestDto {

    private Long roomId;
    private String senderName;
    private String message;
}
