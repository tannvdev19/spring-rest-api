package com.tannv.jobhunter.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private boolean success;
    private T data;
    private String errorMessage;
    private List<String> errorDetails;

    public ApiResponse(HttpStatus status) {
        this.status = status.value();
        this.success = status.is2xxSuccessful();
        if (!this.success) {
            this.timestamp = LocalDateTime.now();
            this.errorMessage = status.getReasonPhrase();
        }
    }

    public ApiResponse(T data, HttpStatus status) {
        this(status);
        this.data = data;
    }

    public ApiResponse(HttpStatus status, Throwable ex) {
        this(status);
        this.errorDetails = Collections.singletonList(ex.getLocalizedMessage());
    }

    public ApiResponse(HttpStatus status, String errorDetail) {
        this(status);
        this.errorDetails = Collections.singletonList(errorDetail);
    }

    public ApiResponse(HttpStatus status, List<String> errorDetails) {
        this(status);
        this.errorDetails = errorDetails;
    }

    public ApiResponse(HttpStatus status, String message, Throwable ex) {
        this(status, ex);
        this.errorMessage = message;
    }

    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(HttpStatus.NO_CONTENT);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(data, HttpStatus.OK);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(data, HttpStatus.CREATED);
    }
}