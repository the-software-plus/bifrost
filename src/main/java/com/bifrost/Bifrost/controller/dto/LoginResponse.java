package com.bifrost.Bifrost.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
