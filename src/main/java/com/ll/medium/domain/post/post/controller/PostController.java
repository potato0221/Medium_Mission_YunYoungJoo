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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
                       @RequestParam(value = "page", defaultValue = "0") int page) {

        Page<Post> paging = this.postService.getList(page);
        model.addAttribute("paging", paging);
        return "post/post/post_list";
    }

    @GetMapping("/myList")
    public String myList(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Principal principal
    ) {
        SiteMember siteMember = this.memberService.getUser(principal.getName());
        Page<Post> paging = this.postService.getListByUsername(page, siteMember);
        model.addAttribute("paging", paging);
        return "post/post/post_list";
    }


    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        Post post = this.postService.getPost(id);
        model.addAttribute("post", post);
        if (post.isPremium()) {
            if(!rq.isPremium()){
                return "redirect:/post/access_denied";
            }
        }else if (post.isPublished()){
            if(rq.getMember()!=post.getAuthor()){
                return "redirect:/post/access_denied";
            }
        }
        return "post/post/post_detail";

    }

    @GetMapping("/access_denied")
    public String accessDenied(){
        return "global/access_denied";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String postCreate(PostForm postForm) {
        return "post/post/post_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String postCreate(@Valid PostForm postForm,
                             BindingResult bindingResult) {
        SiteMember member=rq.getMember();
        member.setCount(member.getCount()+1);
        memberService.save(member);
        if (bindingResult.hasErrors()) {
            return "post/post/post_form";
        }

        this.postService.create(postForm.getTitle(), postForm.getContent(), rq.getMember(), postForm.isPremium(), postForm.isPublished(), member.getCount());
        return "redirect:/post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(
            PostForm postForm,
            @PathVariable("id") Integer id,
            Principal principal
    ) {
        Post post = this.postService.getPost(id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        postForm.setTitle(post.getTitle());
        postForm.setContent(post.getContent());
        postForm.setPremium(post.isPremium());
        postForm.setPublished(post.isPublished());
        return "post/post/post_form";

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid PostForm postForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "post/post/post_form";
        }
        Post post = this.postService.getPost(id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.postService.modify(post, postForm.getTitle(), postForm.getContent(), postForm.isPremium(), postForm.isPublished());
        return String.format("redirect:/post/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String postDelete(
            Principal principal,
            @PathVariable("id") Integer id
    ) {
        Post post = this.postService.getPost(id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.postService.delete(post);
        return "redirect:/";

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/like")
    public String postVote(Principal principal, @PathVariable("id") Integer id) {
        Post post = this.postService.getPost(id);
        SiteMember siteMember = this.memberService.getUser(principal.getName());
        this.postService.vote(post, siteMember);
        return String.format("redirect:/post/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/cancelLike")
    public String deletePostVote(Principal principal, @PathVariable("id") Integer id) {
        Post post = this.postService.getPost(id);
        SiteMember siteMember = this.memberService.getUser(principal.getName());
        this.postService.deleteVote(post, siteMember);
        return String.format("redirect:/post/detail/%s", id);
    }

}
