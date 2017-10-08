package example.packt.com.dynamicregisterapp.client.registration;

public interface OnClientRegistrationResult {
    void onSuccessfulClientRegistration(ClientCredentials credentials);
    void onFailedClientRegistration(String s, Throwable t);
}
