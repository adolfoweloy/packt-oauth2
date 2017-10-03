package example.packt.com.resourceownerpassword.client.profile;


import java.util.Observable;
import java.util.Observer;

import example.packt.com.resourceownerpassword.client.oauth2.AccessToken;

/**
 * If an authorization happened, this class will be aware and will start loading
 * user profile.
 */
public class ProfileAuthorizationListener implements Observer {

    private final UserProfileService userProfileService;

    public ProfileAuthorizationListener(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Override
    public void update(Observable observable, Object o) {

        if (o instanceof AccessToken) {
            AccessToken accessToken = (AccessToken) o;
            userProfileService.load(accessToken);

        } else {
            throw new RuntimeException("Invalid access requestToken being observed");
        }

    }

}
