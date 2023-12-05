package com.ll.medium.domain.home.home.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    // TODO 나중에 redirect 해서 list 등으로 연결
    @GetMapping("/")
    public String showMain() {
        return "domain/home/home/main";
    }
}