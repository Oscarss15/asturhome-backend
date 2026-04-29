package com.asturhome.backend.dto;

public record LoginResponse(String token, String role, String email, String name, String avatarUrl, String preferencias) {}
