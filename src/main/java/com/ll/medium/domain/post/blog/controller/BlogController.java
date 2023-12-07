package com.ll.medium.domain.post.blog.controller;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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

        SiteMember siteMember = this.memberService.getUser(username);
        Page<Post> paging = this.postService.getListByUsername(page, siteMember);


        model.addAttribute("username", username);
        model.addAttribute("paging", paging);
        return "post/post/own_page";
    }

    @GetMapping(value = "/{username}/{id}")
    public String detail(Model model,
                         @PathVariable("id") Integer id,
                         @PathVariable("username") String username,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        SiteMember siteMember = this.memberService.getUser(username);

        Post post = this.postService.getPostByCountByMemberAndMember(siteMember, id);

        model.addAttribute("username", username);
        model.addAttribute("post", post);

        if (post.isPremium()) {
            if (!rq.isPremium()) {
                return "redirect:/post/access_denied";
            }
        } else if (post.isPublished()) {
            if (rq.getMember() != post.getAuthor()) {
                return "redirect:/post/access_denied";
            }
        }

        String postId = String.valueOf(id);
        Cookie[] cookies = request.getCookies();
        boolean viewed = false;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("viewedPosts") && cookie.getValue().contains(postId)) {
                    viewed = true;
                    break;
                }
            }
        }
        if (!viewed) {
            postService.incrementPostViewCount(post);

            // 중복 조회 방지를 위해 쿠키에 게시물 ID 저장
            Cookie cookie = new Cookie("viewedPosts", postId);
            cookie.setMaxAge(1 * 60 * 60); // 쿠키 유지 시간 1시간
            response.addCookie(cookie);
        }

        return "post/post/own_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{username}/vote/{id}")
    public String postVote(@PathVariable("id") Integer id,
                               @PathVariable("username") String username) {
        SiteMember siteMember = this.memberService.getUser(username);

        Post post = this.postService.getPostByCountByMemberAndMember(siteMember, id);

        this.postService.vote(post, siteMember);
        return String.format("redirect:/b/%s/%s",username, id);
    }

}
