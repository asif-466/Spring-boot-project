package com.asif.token.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
    public class DtoApiResponse {
        private String status;
        private String message;
        private Object data;
}
