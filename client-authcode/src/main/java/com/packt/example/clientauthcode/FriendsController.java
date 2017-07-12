package com.packt.example.clientauthcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.packt.example.clientauthcode.FacebookAuthorizationCode.OAuth2TokenResponse;

@Controller
public class FriendsController {

	@Autowired
	private FacebookAuthorizationCode authCodeService;

	@GetMapping("/callback")
	public ModelAndView callback(String code, String state) {
		OAuth2TokenResponse accessToken = authCodeService.getAccessToken(code);

		System.out.println(accessToken.getAccessToken());

		ModelAndView mv = new ModelAndView("friends");
		return mv;
	}

	@GetMapping
	@RequestMapping("/friends")
	public ModelAndView friends() {
		String baseUrl = "https://www.facebook.com/v2.9/dialog/oauth";
		String facebookAuthURL = authCodeService
			.createAuthorizationUrl(baseUrl);

		return new ModelAndView("redirect:" + facebookAuthURL);
	}

}
