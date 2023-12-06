package com.ll.medium.domain.post.premium.controller;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.premium.dto.PremiumPostForm;
import com.ll.medium.domain.post.premium.entity.PremiumPost;
import com.ll.medium.domain.post.premium.service.PremiumPostService;
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

@RequestMapping("/premium/post")
@RequiredArgsConstructor
@Controller
public class PremiumPostController {

    private final PremiumPostService premiumPostService;
    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value="page",defaultValue = "0") int page){
        Page<PremiumPost> paging=this.premiumPostService.getList(page);
        model.addAttribute("paging",paging);
        return "post/premium/premium_post_list";
    }


    @GetMapping("/myList")
    public String myList(
            Model model,
            @RequestParam(value = "page",defaultValue = "0") int page,
            Principal principal
    ){
        SiteMember siteMember = this.memberService.getUser(principal.getName());
        Page<PremiumPost> paging=this.premiumPostService.getListByUsername(page,siteMember);
        model.addAttribute("paging",paging);
        return "post/premium/premium_post_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        PremiumPost premiumPost = this.premiumPostService.getPost(id);
        model.addAttribute("premiumPost", premiumPost);
        return "post/premium/premium_post_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String postCreate(PremiumPostForm premiumPostForm){
        return "post/premium/premium_post_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String postCreate(@Valid PremiumPostForm premiumPostForm,
                             BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "post/premium/premium_post_form";
        }
        this.premiumPostService.create(premiumPostForm.getTitle(),premiumPostForm.getContent(),rq.getMember());
        return "redirect:/premium/post/list";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(
            PremiumPostForm premiumPostForm,
            @PathVariable("id") Integer id,
            Principal principal
    ) {
        PremiumPost premiumPost = this.premiumPostService.getPost(id);
        if (!premiumPost.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        premiumPostForm.setTitle(premiumPost.getTitle());
        premiumPostForm.setContent(premiumPost.getContent());
        return "post/premium/premium_post_form";

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid PremiumPostForm premiumPostForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "post/post/post_form";
        }
        PremiumPost premiumPost = this.premiumPostService.getPost(id);
        if (!premiumPost.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.premiumPostService.modify(premiumPost, premiumPostForm.getTitle(), premiumPostForm.getContent());
        return String.format("redirect:/premium/post/detail/%s", id);
    }



}
