package hankyu.board.spring_board.domain.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OauthController {

    @GetMapping("/oauth")
    public String index() {
        return "oauth/Index";
    }
}
