package com.ll.medium.domain.home.home.controller;

import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final PostService postService;

    @GetMapping("/")
    public String recentList(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Post> paging = this.postService.getRecent30(page);
        model.addAttribute("paging", paging);
        return "post/post/recent_post";
    }


}