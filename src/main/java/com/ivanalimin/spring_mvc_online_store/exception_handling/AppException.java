package com.ivanalimin.spring_mvc_online_store.exception_handling;

import org.springframework.lang.NonNull;

public class AppException extends RuntimeException {
    public AppException(@NonNull String message) {
        super(message);
    }
}
