package com.rookies3.genaiquestionapp.exception.global;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponse<T> {
    private boolean success;
    private int code;  // <-- HTTP 상태코드 추가
    private T data;
    private String message;
    private LocalDateTime timestamp;
    private String requestId;
}
