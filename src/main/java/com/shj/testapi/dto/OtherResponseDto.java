package com.shj.testapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class OtherResponseDto {

    private Long otherId;
    private String senderName;
    private String data;
    private LocalDateTime createdTime;

    public OtherResponseDto(OtherRequestDto otherRequestDto, Long otherId, String data, LocalDateTime createdTime) {
        this.otherId = otherId;
        this.senderName = otherRequestDto.getSenderName();
        this.data = data;
        this.createdTime = createdTime;
    }
}
