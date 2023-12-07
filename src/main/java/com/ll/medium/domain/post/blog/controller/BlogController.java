package com.ll.medium.domain.post.blog.controller;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/b")
@RequiredArgsConstructor
@Controller
public class BlogController {

    private final PostService postService;
    private final MemberService memberService;
    private final Rq rq;

    @GetMapping(value = "/{username}")
    public String getPostsByUser(
            Model model,
            @PathVariable("username") String username,
            @RequestParam(value = "page", defaultValue = "0") int page) {

        SiteMember siteMember =this.memberService.getUser(username);
        Page<Post> paging = this.postService.getListByUsername(page,siteMember);


        model.addAttribute("username", username);
        model.addAttribute("paging", paging);
        return "post/post/own_page";
    }

    @GetMapping(value = "/{username}/{id}")
    public String detail(Model model,
                         @PathVariable("id") Integer id,
                         @PathVariable("username") String username) {
        SiteMember siteMember=this.memberService.getUser(username);
        Post post = this.postService.getPost(id);

        model.addAttribute("username", username);
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
    


}
