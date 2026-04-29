package com.asturhome.backend.dto;

public record ResetPasswordRequest(String token, String newPassword) {}
