package com.packt.example.popclient;

import java.net.URI;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.packt.example.popclient.oauth.AuthorizationCodeTokenService;
import com.packt.example.popclient.oauth.OAuth2ClientTokenSevices;

@Controller
public class UserDashboard {

    @Autowired
    private AuthorizationCodeTokenService tokenService;

    @Autowired
    private OAuth2ClientTokenSevices clientTokenServices;

    @Autowired
    private OAuth2ProtectedResourceDetails resourceDetails;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/callback")
    public ModelAndView callback(String code, String state) {
        OAuth2AccessToken token = tokenService.getToken(code);
        clientTokenServices.saveAccessToken(resourceDetails, SecurityContextHolder.getContext().getAuthentication(), token);
        return new ModelAndView("forward:/dashboard");
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        List<Entry> entries = Arrays.asList(
                new Entry("entry 1"),
                new Entry("entry 2"));

        ModelAndView mv = new ModelAndView("dashboard");
        mv.addObject("entries", entries);

        /// OAuth request
        String endpoint = "http://localhost:8081/api/profile";
        try {
            OAuth2AccessToken accessToken = clientTokenServices.getAccessToken(
                resourceDetails, SecurityContextHolder.getContext().getAuthentication());

            if (accessToken == null) {
                String authEndpoint = tokenService.getAuthorizationEndpoint();
                return new ModelAndView("redirect:" + authEndpoint);
            }

            String jwsToken = createJws(accessToken);

            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Authorization", "Bearer " + jwsToken);

            RequestEntity<Object> request = new RequestEntity<>(
                    headers, HttpMethod.GET, URI.create(endpoint));

            ResponseEntity<UserProfile> userProfile = restTemplate.exchange(request, UserProfile.class);

            mv.addObject("profile", userProfile.getBody());
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("it was not possible to retrieve user profile");
        }

        /// End of OAuth request

        return mv;
    }

    private String createJws(OAuth2AccessToken accessToken) {
        try {

            // signing the request with RSA in such a way that the resource server
            // can validates that this client possesses the private key issued by the authorization server.
            JWK jwk = JWK.parse((String) accessToken.getAdditionalInformation().get("access_token_key"));

            Map<String, String> payloadContent = new HashMap<String, String>();
            payloadContent.put("at", accessToken.getValue());
            payloadContent.put("u", "localhost:9000");
            JSONObject json = new JSONObject(payloadContent);

            RSASSASigner signer = new RSASSASigner((RSAKey) jwk);
            JWSObject jwsObject = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(jwk.getKeyID()).build(),
                new Payload(json));

            jwsObject.sign(signer);
            return jwsObject.serialize();

        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

}
