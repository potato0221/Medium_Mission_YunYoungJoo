package com.ll.medium.domain.comment.comment.controller;


import com.ll.medium.domain.comment.comment.dto.CommentForm;
import com.ll.medium.domain.comment.comment.entity.Comment;
import com.ll.medium.domain.comment.comment.service.CommentService;
import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@RequestMapping("/b/comment")
@RequiredArgsConstructor
@Controller
public class BlogCommentController {

    private final PostService postService;
    private final CommentService commentService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{username}/{id}/create")
    public String createComment(Model model,
                                @PathVariable("id") Integer id,
                                @PathVariable("username") String username,
                                @Valid CommentForm commentForm,
                                BindingResult bindingResult,
                                Principal principal
    ) {
        SiteMember siteMember1 = this.memberService.getUser(username);
        Post post = this.postService.getPostByCountByMemberAndMember(siteMember1, id);
        SiteMember siteMember2 = this.memberService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("post", post);
            return "post/post/own_detail";
        }

        this.commentService.create(post, commentForm.getContent(), siteMember2);
        return String.format("redirect:/b/%s/%s", username, id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{username}/{id}/modify/{id2}")
    public String commentModify(CommentForm commentForm,
                                @PathVariable("username") String username,
                                @PathVariable("id") Integer id,
                                @PathVariable("id2") Integer id2,
                                Principal principal) {
        Comment comment = this.commentService.getComment(id2);

        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            return "redirect:/post/access_denied";
        }

        commentForm.setContent(comment.getContent());
        return "comment/comment/comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{username}/{id}/modify/{id2}")
    public String commentModify(
            @Valid CommentForm commentForm,
            @PathVariable("username") String username,
            BindingResult bindingResult,
            @PathVariable("id") Integer id,
            @PathVariable("id2") Integer id2,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            return "comment/comment/comment_form";
        }

        Comment comment = this.commentService.getComment(id2);

        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            return "redirect:/post/access_denied";
        }
        this.commentService.modify(comment, commentForm.getContent());
        return String.format("redirect:/b/%s/%s", username, id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{username}/{id}/delete/{id2}") //username/postNum/delete/commentNum
    public String commentDelete(Principal principal,
                                @PathVariable("username") String username,
                                @PathVariable("id") Integer id,
                                @PathVariable("id2") Integer id2) {

        Comment comment = this.commentService.getComment(id2);

        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            return "redirect:/post/access_denied";
        }
        this.commentService.delete(comment);
        return String.format("redirect:/b/%s/%s", username, id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("{username}/{id}/like/{id2}")
    public String commentVote(Principal principal,
                              @PathVariable("username") String username,
                              @PathVariable("id") Integer id,
                              @PathVariable("id2") Integer id2) {
        SiteMember siteMember=this.memberService.getUser(principal.getName());
        Comment comment = this.commentService.getComment(id2);
        this.commentService.vote(comment, siteMember);
        return String.format("redirect:/b/%s/%s", username, id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{username}/{id}/cancelLike/{id2}")
    public String deleteCommentVote(Principal principal,
                                    @PathVariable("username") String username,
                                    @PathVariable("id") Integer id,
                                    @PathVariable("id2") Integer id2) {

        SiteMember siteMember2=this.memberService.getUser(principal.getName());
        Comment comment = this.commentService.getComment(id2);
        this.commentService.deleteVote(comment, siteMember2);
        return String.format("redirect:/b/%s/%s", username, id);
    }


}
