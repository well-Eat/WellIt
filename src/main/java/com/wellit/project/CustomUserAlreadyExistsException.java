package com.wellit.project;

public class CustomUserAlreadyExistsException extends RuntimeException {
    private final String kakaoUserId;

    public CustomUserAlreadyExistsException(String kakaoUserId) {
        super("이미 가입된 사용자입니다. 카카오 사용자 ID: " + kakaoUserId);
        this.kakaoUserId = kakaoUserId;
    }

    public String getKakaoUserId() {
        return kakaoUserId;
    }
}