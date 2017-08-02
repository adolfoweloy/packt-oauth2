package com.packt.example.clientimplicit.user;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import com.packt.example.clientimplicit.oauth.UserProfile;
import com.packt.example.clientimplicit.security.ClientUserDetails;

@Controller
public class UserDashboard {
    //@formatter:off

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @GetMapping("/callback")
    public ModelAndView callback() {
        ClientUser clientUser = getClientUserData();

        ModelAndView mv = new ModelAndView("dashboard");
        mv.addObject("user", clientUser);

        return mv;
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        ClientUser clientUser = getClientUserData();

        ModelAndView mv = new ModelAndView("dashboard");
        mv.addObject("user", clientUser);

        tryToGetUserProfile(mv);

        return mv;
    }

    private ClientUser getClientUserData() {
        ClientUserDetails userDetails = (ClientUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        ClientUser clientUser = userDetails.getClientUser();

        clientUser.setEntries(Arrays.asList(
                new Entry("entry 1"),
                new Entry("entry 2")));

        return clientUser;
    }

    private void tryToGetUserProfile(ModelAndView mv) {
        String endpoint = "http://localhost:8081/api/profile";
        try {
            UserProfile userProfile = restTemplate.getForObject(endpoint, UserProfile.class);
            mv.addObject("profile", userProfile);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("it was not possible to retrieve user profile");
        }
    }

    //@formatter:on
}
