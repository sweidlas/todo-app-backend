package com.example.graphqlserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginPageController {

    // TODO: remove

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Returns login.html template
    }

    @GetMapping("/graphiql2")
    public String graphiql() {
        return "graphiql";
    }
}
