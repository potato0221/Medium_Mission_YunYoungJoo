package com.ll.medium.domain.post.post.controller;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

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

    @GetMapping("/myList")
    public String myList(
            Model model,
            @RequestParam(value = "page",defaultValue = "0") int page,
            Principal principal
    ){
        SiteMember siteMember = this.memberService.getUser(principal.getName());
        Page<Post> paging=this.postService.getListByUsername(page,siteMember);
        model.addAttribute("paging",paging);
        return "/post/post/post_list";
    }

}
