package com.cinect.service;

import com.cinect.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Handles OAuth2 token exchange and profile fetching for Google, Facebook, and GitHub.
 */
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Google
    @Value("${oauth.google.client-id:}")
    private String googleClientId;
    @Value("${oauth.google.client-secret:}")
    private String googleClientSecret;
    @Value("${oauth.google.redirect-uri:http://localhost:8081/api/v1/auth/google/callback}")
    private String googleRedirectUri;

    // Facebook
    @Value("${oauth.facebook.app-id:}")
    private String facebookAppId;
    @Value("${oauth.facebook.app-secret:}")
    private String facebookAppSecret;
    @Value("${oauth.facebook.redirect-uri:http://localhost:8081/api/v1/auth/facebook/callback}")
    private String facebookRedirectUri;

    // GitHub
    @Value("${oauth.github.client-id:}")
    private String githubClientId;
    @Value("${oauth.github.client-secret:}")
    private String githubClientSecret;
    @Value("${oauth.github.redirect-uri:http://localhost:8081/api/v1/auth/github/callback}")
    private String githubRedirectUri;

    // ==========================
    // Google
    // ==========================

    public String getGoogleAuthUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + googleClientId +
                "&redirect_uri=" + googleRedirectUri +
                "&response_type=code" +
                "&scope=openid%20email%20profile" +
                "&access_type=offline";
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getGoogleUserProfile(String code) {
        // Exchange code for token
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token", request, Map.class);

        if (!tokenResponse.getStatusCode().is2xxSuccessful() || tokenResponse.getBody() == null) {
            throw new BadRequestException("Failed to exchange Google auth code");
        }

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // Get user profile
        HttpHeaders profileHeaders = new HttpHeaders();
        profileHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> profileRequest = new HttpEntity<>(profileHeaders);

        ResponseEntity<Map> profileResponse = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET, profileRequest, Map.class);

        if (!profileResponse.getStatusCode().is2xxSuccessful() || profileResponse.getBody() == null) {
            throw new BadRequestException("Failed to fetch Google user profile");
        }

        Map<String, Object> profile = profileResponse.getBody();
        return Map.of(
                "provider", "GOOGLE",
                "providerId", String.valueOf(profile.get("id")),
                "email", profile.getOrDefault("email", ""),
                "fullName", profile.getOrDefault("name", ""),
                "avatar", profile.getOrDefault("picture", "")
        );
    }

    // ==========================
    // Facebook
    // ==========================

    public String getFacebookAuthUrl() {
        return "https://www.facebook.com/v18.0/dialog/oauth" +
                "?client_id=" + facebookAppId +
                "&redirect_uri=" + facebookRedirectUri +
                "&scope=email,public_profile" +
                "&response_type=code";
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getFacebookUserProfile(String code) {
        // Exchange code for token
        String tokenUrl = "https://graph.facebook.com/v18.0/oauth/access_token" +
                "?client_id=" + facebookAppId +
                "&client_secret=" + facebookAppSecret +
                "&redirect_uri=" + facebookRedirectUri +
                "&code=" + code;

        ResponseEntity<Map> tokenResponse = restTemplate.getForEntity(tokenUrl, Map.class);

        if (!tokenResponse.getStatusCode().is2xxSuccessful() || tokenResponse.getBody() == null) {
            throw new BadRequestException("Failed to exchange Facebook auth code");
        }

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // Get user profile
        String profileUrl = "https://graph.facebook.com/me?fields=id,name,email,picture.type(large)&access_token=" + accessToken;
        ResponseEntity<Map> profileResponse = restTemplate.getForEntity(profileUrl, Map.class);

        if (!profileResponse.getStatusCode().is2xxSuccessful() || profileResponse.getBody() == null) {
            throw new BadRequestException("Failed to fetch Facebook user profile");
        }

        Map<String, Object> profile = profileResponse.getBody();
        String avatar = "";
        if (profile.get("picture") instanceof Map pictureMap) {
            if (pictureMap.get("data") instanceof Map dataMap) {
                avatar = String.valueOf(dataMap.getOrDefault("url", ""));
            }
        }

        return Map.of(
                "provider", "FACEBOOK",
                "providerId", String.valueOf(profile.get("id")),
                "email", String.valueOf(profile.getOrDefault("email", "")),
                "fullName", String.valueOf(profile.getOrDefault("name", "")),
                "avatar", avatar
        );
    }

    // ==========================
    // GitHub
    // ==========================

    public String getGithubAuthUrl() {
        return "https://github.com/login/oauth/authorize" +
                "?client_id=" + githubClientId +
                "&redirect_uri=" + githubRedirectUri +
                "&scope=user:email";
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getGithubUserProfile(String code) {
        // Exchange code for token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, String> tokenBody = Map.of(
                "client_id", githubClientId,
                "client_secret", githubClientSecret,
                "code", code,
                "redirect_uri", githubRedirectUri
        );

        HttpEntity<Map<String, String>> tokenRequest = new HttpEntity<>(tokenBody, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                "https://github.com/login/oauth/access_token", tokenRequest, Map.class);

        if (!tokenResponse.getStatusCode().is2xxSuccessful() || tokenResponse.getBody() == null) {
            throw new BadRequestException("Failed to exchange GitHub auth code");
        }

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // Get user profile
        HttpHeaders profileHeaders = new HttpHeaders();
        profileHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> profileRequest = new HttpEntity<>(profileHeaders);

        ResponseEntity<Map> profileResponse = restTemplate.exchange(
                "https://api.github.com/user", HttpMethod.GET, profileRequest, Map.class);

        if (!profileResponse.getStatusCode().is2xxSuccessful() || profileResponse.getBody() == null) {
            throw new BadRequestException("Failed to fetch GitHub user profile");
        }

        Map<String, Object> profile = profileResponse.getBody();

        // Get email if not public
        String email = profile.get("email") != null ? String.valueOf(profile.get("email")) : "";
        if (email.isEmpty()) {
            ResponseEntity<List> emailsResponse = restTemplate.exchange(
                    "https://api.github.com/user/emails", HttpMethod.GET, profileRequest, List.class);
            if (emailsResponse.getStatusCode().is2xxSuccessful() && emailsResponse.getBody() != null) {
                for (Object item : emailsResponse.getBody()) {
                    if (item instanceof Map emailMap && Boolean.TRUE.equals(emailMap.get("primary"))) {
                        email = String.valueOf(emailMap.get("email"));
                        break;
                    }
                }
            }
        }

        return Map.of(
                "provider", "GITHUB",
                "providerId", String.valueOf(profile.get("id")),
                "email", email,
                "fullName", String.valueOf(profile.getOrDefault("name", profile.getOrDefault("login", ""))),
                "avatar", String.valueOf(profile.getOrDefault("avatar_url", ""))
        );
    }
}
