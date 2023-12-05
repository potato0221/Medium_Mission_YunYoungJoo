package com.ll.medium.domain.article.article.controller;

import com.ll.medium.domain.article.article.entity.Post;
import com.ll.medium.domain.article.article.service.PostService;
import com.ll.medium.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/post")
@RequiredArgsConstructor
@Controller
public class PostController {

    private final PostService postService;
    private final MemberService memberService;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value="page",defaultValue = "0") int page){
        Page<Post> paging=this.postService.getList(page);
        model.addAttribute("paging",paging);
        return "post/post/post_list";
    }



}
