package com.rookies3.genaiquestionapp.exception.global;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomError {
    private String code;
    private String message;
    private List<com.rookies3.genaiquestionapp.exception.global.ErrorDetail> details;
    private String path;
    private String method;
}