package auth_api.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/profile")
    public String profile(Authentication authentication) {

        return "Hello " + authentication.getName();
    }
}