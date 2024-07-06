package com.tannv.jobhunter.domain.dto;

public class ResLoginDTO {
    private String accessToken;

    public ResLoginDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
