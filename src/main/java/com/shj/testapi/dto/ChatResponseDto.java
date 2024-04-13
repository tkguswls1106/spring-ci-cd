package com.shj.testapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatResponseDto {

    private Long roomId;
    private String senderName;
    private String message;
    private LocalDateTime createdTime;

    public ChatResponseDto(ChatRequestDto chatRequestDto, String message, LocalDateTime createdTime) {
        this.roomId = chatRequestDto.getRoomId();
        this.senderName = chatRequestDto.getSenderName();
        this.message = message;
        this.createdTime = createdTime;
    }
}
