package fr.focusflow.dtos;

import org.springframework.http.ResponseCookie;

public record CookieDTO(ResponseCookie accessCookie, ResponseCookie refreshCookie, ResponseCookie csrfCookie) {
}
