package com.packt.example.googleconnect.user;

import com.packt.example.googleconnect.openid.OpenIDAuthentication;
import com.packt.example.googleconnect.openid.OpenIDAuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private OpenIDAuthenticationRepository openIDRepository;

    @GetMapping
    public ModelAndView profile() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Profile> profile = profileRepository.findByUser(user);

        if (profile.isPresent()) {
            ModelAndView mv = new ModelAndView("profile");
            mv.addObject("profile", profile.get());
            Optional<OpenIDAuthentication> openID = openIDRepository.findByUser(user);
            if (openID.isPresent()) {
                mv.addObject("openID", openID.get());
            }
            return mv;
        }

        return new ModelAndView("redirect:/profile/form");
    }

    @GetMapping("/form")
    public ModelAndView form() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Profile> profile = profileRepository.findByUser(user);

        ModelAndView mv = new ModelAndView("form");
        if (profile.isPresent()) {
            mv.addObject("profile", profile.get());
        } else {
            mv.addObject("profile", new Profile());
        }

        return mv;
    }

    @PostMapping
    public ModelAndView save(Profile profile) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        profile.setUser(user);

        Profile newProfile = profileRepository.save(profile);
        ModelAndView mv = new ModelAndView("redirect:/profile");
        mv.addObject("profile", newProfile);
        return mv;
    }
}
