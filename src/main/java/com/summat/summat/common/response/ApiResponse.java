package com.summat.summat.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;
    private HttpStatus httpStatus;
    private String message;
    private T data;

    public ApiResponse(ResponseCode responseCode, T data) {
        this.code = responseCode.getCode();
        this.httpStatus = responseCode.getHttpStatus();
        this.message = responseCode.getMessage();
        this.data = data;
    }
}
