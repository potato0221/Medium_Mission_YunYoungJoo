package com.ll.medium.domain.post.premium.controller;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.premium.entity.PremiumPost;
import com.ll.medium.domain.post.premium.service.PremiumPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RequestMapping("/premium")
@RequiredArgsConstructor
@Controller
public class PremiumPostController {

    private final PremiumPostService premiumPostService;
    private final MemberService memberService;

    @GetMapping("/post/list")
    public String list(Model model,
                       @RequestParam(value="page",defaultValue = "0") int page){
        Page<PremiumPost> paging=this.premiumPostService.getList(page);
        model.addAttribute("paging",paging);
        return "post/post/post_list";
    }


    @GetMapping("/post/myList")
    public String myList(
            Model model,
            @RequestParam(value = "page",defaultValue = "0") int page,
            Principal principal
    ){
        SiteMember siteMember = this.memberService.getUser(principal.getName());
        Page<PremiumPost> paging=this.premiumPostService.getListByUsername(page,siteMember);
        model.addAttribute("paging",paging);
        return "/post/post/post_list";
    }

}
