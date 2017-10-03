package example.packt.com.resourceownerpassword.client.profile;

/**
 * Decouples the user profile retrieval from view presenter.
 */
public interface OnProfileResultCallback {
    void onSuccess(UserProfile userProfile);
    void onError(String message, Throwable t);
}
