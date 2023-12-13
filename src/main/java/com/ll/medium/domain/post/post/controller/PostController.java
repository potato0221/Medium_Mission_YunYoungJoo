package com.ll.medium.domain.post.post.controller;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.post.dto.PostForm;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping("/post")
@RequiredArgsConstructor
@Controller
public class PostController {

    private final PostService postService;
    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kwTypes", defaultValue = "title,content") List<String> kwTypes,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "sort", defaultValue = "desc") String sort) {

        Page<Post> paging;
        if (kwTypes != null && !kwTypes.isEmpty() && kw != null) {
            paging = this.postService.search(kwTypes, kw, sort, PageRequest.of(page, 9));
        } else {
            paging = this.postService.getList(page);
        }


        String kwTypesParam = String.join(",", kwTypes); // 배열을 문자열로 변환

        model.addAttribute("kw", kw);
        model.addAttribute("kwTypes", kwTypesParam); // kwTypes를 문자열로 변환한 값을 추가
        model.addAttribute("paging", paging);
        model.addAttribute("sort", sort);
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
        return "post/post/post_myList";
    }


    @GetMapping(value = "/detail/{id}")
    public String detail(Model model,
                         @PathVariable("id") Integer id,
                         HttpServletRequest request,
                         HttpServletResponse response
    ) {
        Post post = this.postService.getPost(id);
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
        // 쿠키 또는 세션을 사용하여 중복 요청 필터링
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


        return "post/post/post_detail";

    }

    @GetMapping("/access_denied")
    public String accessDenied() {
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
        SiteMember member = rq.getMember();
        member.setCount(member.getCount() + 1);
        this.memberService.save(member);
        if (bindingResult.hasErrors()) {
            return "post/post/post_form";
        }

        this.postService.create(postForm.getTitle(), postForm.getContent(), rq.getMember(), postForm.isPremium(), postForm.isPublished(), member.getCount(), 0);
        return "redirect:/post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String postModify(
            PostForm postForm,
            @PathVariable("id") Integer id,
            Principal principal
    ) {
        Post post = this.postService.getPost(id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            return "redirect:/post/access_denied";
        }
        postForm.setTitle(post.getTitle());
        postForm.setContent(post.getContent());
        postForm.setPremium(post.isPremium());
        postForm.setPublished(post.isPublished());
        return "post/post/post_form";

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String postModify(@Valid PostForm postForm, BindingResult bindingResult,
                             Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "post/post/post_form";
        }
        Post post = this.postService.getPost(id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            return "redirect:/post/access_denied";
        }
        this.postService.modify(post, postForm.getTitle(), postForm.getContent(), postForm.isPremium(), postForm.isPublished());
        return String.format("redirect:/post/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{id}")
    public String postDelete(
            @PathVariable("id") Integer id
    ) {
        Post post = this.postService.getPost(id);
        if (!postService.canDelete(rq.getMember(),post)) {
            return "redirect:/post/access_denied";
        }
        this.postService.delete(post);

        return rq.redirect("/", "%s번 게시물이 삭제되었습니다.".formatted(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/like")
    public String postVote(Principal principal, @PathVariable("id") Integer id) {
        Post post = this.postService.getPost(id);
        SiteMember siteMember = this.memberService.getUser(principal.getName());
        this.postService.vote(post, siteMember);
        return rq.redirect("/post/detail/%s".formatted(id), "추천 되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}/cancelLike")
    public String deletePostVote(Principal principal, @PathVariable("id") Integer id) {
        Post post = this.postService.getPost(id);
        SiteMember siteMember = this.memberService.getUser(principal.getName());
        this.postService.deleteVote(post, siteMember);
        return rq.redirect("/post/detail/%s".formatted(id), "추천이 취소 되었습니다.");
    }

}
