package hankyu.board.spring_board.domain.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/oauth")
public class OauthController {

    @GetMapping()
    public String index() {
        return "/oauth/index";
    }
}
