package com.packt.example.clientauthcode;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class FacebookAuthorizationCode {

	@Autowired
	private AuthorizationData userAuthData;

	public OAuth2TokenResponse getAccessToken(String code) {
		RestTemplate rest = new RestTemplate();

		String clientId = "111916466114903";
		String clientSecret =  "cd6d137f423f84b677880fa89a41916c";

		RequestEntity<MultiValueMap<String, String>> requestEntity = new RequestEntity<>(
				getBody(code),
				getHeader(base64Auth(clientId, clientSecret)),
				HttpMethod.POST,
				URI.create("https://graph.facebook.com/v2.9/oauth/access_token"));

		ResponseEntity<OAuth2TokenResponse> responseEntity = rest.exchange(requestEntity, OAuth2TokenResponse.class);

		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			return responseEntity.getBody();
		}

		throw new RuntimeException("Error trying to request an access token");
	}

	public String base64Auth(String clientID, String clientSecret) {
		String rawCredentials = clientID + ":" + clientSecret;
		return Base64.getEncoder()
			.encodeToString(rawCredentials.getBytes());
	}

	private HttpHeaders getHeader(String clientAuthentication) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		httpHeaders.add("Authorization", "Basic " + clientAuthentication);
		return httpHeaders;
	}

	private MultiValueMap<String, String> getBody(String authorizationCode) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", "authorization_code");
        formData.add("code", authorizationCode);
        formData.add("redirect_uri", urlEncoded("http://localhost:8080/callback"));
        formData.add("scope", "public_profile user_friends");

        return formData;
    }

	public static class OAuth2TokenResponse {
		private String accessToken;
		private String tokenType;
		private String expiresIn;

		public String getAccessToken() {
			return accessToken;
		}
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
		public String getTokenType() {
			return tokenType;
		}
		public void setTokenType(String tokenType) {
			this.tokenType = tokenType;
		}
		public String getExpiresIn() {
			return expiresIn;
		}
		public void setExpiresIn(String expiresIn) {
			this.expiresIn = expiresIn;
		}

	}

	public String createAuthorizationUrl(String facebookAuthURL) {
		Map<String, String> params = new HashMap<>();
		params.put("response_type", "code");
		params.put("redirect_uri", urlEncoded("http://localhost:8080/callback"));
		params.put("scope", urlEncoded("public_profile user_friends"));
		params.put("client_id", urlEncoded("111916466114903"));

		String state = getRandomString();
		params.put("state", urlEncoded(state));

		userAuthData.setAuthorizationCode(state);

		return buildParameters(facebookAuthURL, params);
	}

	private String urlEncoded(String content) {
		try {
			return URLEncoder.encode(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private String buildParameters(String endpoint, Map<String, String> parameters) {
        List<String> authParameters = new ArrayList<>(parameters.size());

        parameters.forEach((param, value) -> {
            authParameters.add(param + "=" + value);
        });

        return endpoint + "?" + authParameters.stream()
            .reduce((a,b) -> a + "&" + b).get();
    }

	private String getRandomString() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}


}
