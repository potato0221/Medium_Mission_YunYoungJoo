package com.ll.medium.domain.post.post.controller;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.post.dto.PostForm;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/post")
@RequiredArgsConstructor
@Controller
public class PostController {

    private final PostService postService;
    private final MemberService memberService;
    private final Rq rq;

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
        return "post/post/post_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String postCreate(PostForm postForm){
        return "post/post/post_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String postCreate(@Valid PostForm postForm,
                             BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "post/post/post_form";
        }
        this.postService.create(postForm.getTitle(),postForm.getContent(),rq.getMember());
        return "redirect:/post/list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        Post post = this.postService.getPost(id);
        model.addAttribute("post", post);
        return "post/post/post_detail";
    }

}
