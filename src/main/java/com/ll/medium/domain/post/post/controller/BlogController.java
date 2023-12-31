package com.ll.medium.domain.post.post.controller;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.post.dto.PostForm;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

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
        Page<Post> paging = this.postService.getOwnListByUsername(page, siteMember);


        model.addAttribute("username", username);
        model.addAttribute("nickname", siteMember.getNickname());
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
        model.addAttribute("post", post);

        if (post.isNotPublished()) {
            if (rq.getMember() != post.getAuthor()) {
                return rq.redirectIfAccessError("/post/access_denied", "접근 권한이 없는 글 입니다.");
            }
        }

        HttpSession session = request.getSession();
        Set<Integer> viewedPosts = (Set<Integer>) session.getAttribute("viewedPosts");

        if (viewedPosts == null) {
            viewedPosts = new HashSet<>();
            session.setAttribute("viewedPosts", viewedPosts);
        }

        if (!viewedPosts.contains(id)) {
            postService.incrementPostViewCount(post);
            viewedPosts.add(id);
        }

        return "post/post/own_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{username}/{id}/like")
    public String postVote(@PathVariable("id") Integer id,
                           @PathVariable("username") String username) {
        SiteMember siteMember = this.memberService.getUser(username);
        SiteMember siteMember2 = rq.getMember();
        Post post = this.postService.getPostByCountByMemberAndMember(siteMember, id);

        this.postService.vote(post, siteMember2);
        return rq.redirect("/b/%s/%s".formatted(username, id), "추천 되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{username}/{id}/cancelLike")
    public String deletePostVote(@PathVariable("id") Integer id,
                                 @PathVariable("username") String username) {
        SiteMember siteMember = this.memberService.getUser(username);
        SiteMember siteMember2 = rq.getMember();
        Post post = this.postService.getPostByCountByMemberAndMember(siteMember, id);
        this.postService.deleteVote(post, siteMember2);
        return rq.redirect("/b/%s/%s".formatted(username, id), "추천이 취소 되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{username}/{id}/modify")
    public String postModify(
            PostForm postForm,
            @PathVariable("id") Integer id,
            Principal principal,
            @PathVariable("username") String username
    ) {

        SiteMember siteMember = this.memberService.getUser(username);
        Post post = this.postService.getPostByCountByMemberAndMember(siteMember, id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            return rq.redirectIfAccessError("/post/access_denied", "수정 권한이 없는 글 입니다.");
        }
        postForm.setTitle(post.getTitle());
        postForm.setContent(post.getContent());
        postForm.setPaid(post.isPaid());
        postForm.setNotPublished(post.isNotPublished());
        return "post/post/post_modifyForm";

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{username}/{id}/modify")
    public String postModify(@Valid PostForm postForm,
                             BindingResult bindingResult,
                             Principal principal,
                             @PathVariable("id") Integer id,
                             @PathVariable("username") String username) {
        if (bindingResult.hasErrors()) {
            return "post/post/post_form";
        }
        SiteMember siteMember = this.memberService.getUser(username);
        Post post = this.postService.getPostByCountByMemberAndMember(siteMember, id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            return rq.redirectIfAccessError("/post/access_denied", "수정 권한이 없는 글 입니다.");
        }
        this.postService.modify(post, postForm.getTitle(), postForm.getContent(), postForm.isPaid(), postForm.isNotPublished());
        return rq.redirect("/b/%s/%s".formatted(username, id), "게시물이 수정 되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{username}/{id}/delete")
    public String postDelete(
            Principal principal,
            @PathVariable("id") Integer id,
            @PathVariable("username") String username
    ) {
        SiteMember siteMember = this.memberService.getUser(username);
        Post post = this.postService.getPostByCountByMemberAndMember(siteMember, id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            return rq.redirectIfAccessError("/post/access_denied", "삭제 권한이 없는 글 입니다.");
        }
        this.postService.delete(post);
        return rq.redirect("/b/%s".formatted(username), "게시물이 삭제 되었습니다.");

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
        SiteMember member = rq.getMember();
        member.setCount(member.getCount() + 1);
        this.memberService.save(member);
        if (bindingResult.hasErrors()) {
            return "post/post/post_form";
        }

        this.postService.create(member,
                postForm.getTitle(),
                postForm.getContent(),
                rq.getMember(),
                postForm.isPaid(),
                postForm.isNotPublished(),
                member.getCount());

        return rq.redirect("/b/%s".formatted(rq.getMember().getUsername()), "게시물이 등록 되었습니다.");
    }
}
