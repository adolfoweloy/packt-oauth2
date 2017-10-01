package example.packt.com.authcodeapp.oauth2.service;

import java.util.Map;

import example.packt.com.authcodeapp.oauth2.dto.AccessToken;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthorizationCodeService {

    @FormUrlEncoded
    @POST("oauth/token")
    Call<AccessToken> token(@FieldMap Map<String, String> tokenRequest);

}
