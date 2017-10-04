package example.packt.com.dynamicregisterapp.client.oauth2.registration;

public interface OnClientRegistrationResult {
    void onSuccessfulClientRegistration(ClientCredentials credentials);
    void onFailedClientRegistration(String s, Throwable t);
}
