package com.freebie.authservice.dto;

public record LoginResponse(
   String token,
   String role,
   long expiresInSeconds
) {}
