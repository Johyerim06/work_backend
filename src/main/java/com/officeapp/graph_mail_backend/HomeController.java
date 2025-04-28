package com.officeapp.graph_mail_backend;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;


@Controller
public class HomeController {
    @GetMapping("/")
    public String home(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        model.addAttribute("name", oidcUser.getFullName());
        return "index";
    }
}
