package com.ll.medium.domain.post.comment.controller;


import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.comment.dto.CommentForm;
import com.ll.medium.domain.post.comment.entity.Comment;
import com.ll.medium.domain.post.comment.service.CommentService;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final PostService postService;
    private final CommentService commentService;
    private final MemberService memberService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createComment(Model model,
                                @PathVariable("id") Integer id,
                                @Valid CommentForm commentForm,
                                BindingResult bindingResult,
                                Principal principal
    ) {
        Post post = this.postService.getPost(id);
        SiteMember siteMember = this.memberService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("post", post);
            return "post/post/post_detail";
        }

        this.commentService.create(post, commentForm.getContent(), siteMember);
        return rq.redirect("/post/detail/%s".formatted(post.getId()), "댓글이 작성 되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String commentModify(CommentForm commentForm,
                                @PathVariable("id") Integer id,
                                Principal principal) {

        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            return rq.redirectIfAccessError("/post/access_denied", "수정 권한이 없는 댓글 입니다.");
        }
        commentForm.setContent(comment.getContent());
        return "comment/comment/comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String commentModify(
            @Valid CommentForm commentForm,
            BindingResult bindingResult,
            @PathVariable("id") Integer id,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            return "comment/comment/comment_form";
        }
        Comment comment = this.commentService.getComment(id);

        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            return rq.redirectIfAccessError("/post/access_denied", "수정 권한이 없는 댓글 입니다.");
        }

        this.commentService.modify(comment, commentForm.getContent());
        return rq.redirect("/post/detail/%s".formatted(comment.getPost().getId()), "댓글이 수정 되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{id}")
    public String commentDelete(
            @PathVariable("id") Integer id) {
        Comment comment = this.commentService.getComment(id);
        if (!commentService.canDelete(rq.getMember(), comment)) {
            return rq.redirectIfAccessError("/post/access_denied", "삭제 권한이 없는 댓글 입니다.");
        }
        this.commentService.delete(comment);
        return rq.redirect("/post/detail/%s".formatted(comment.getPost().getId()), "댓글이 삭제 되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/like")
    public String commentVote(Principal principal,
                              @PathVariable("id") Integer id) {
        Comment comment = this.commentService.getComment(id);
        SiteMember siteMember = this.memberService.getUser(principal.getName());
        this.commentService.vote(comment, siteMember);
        return rq.redirect("/post/detail/%s".formatted(comment.getPost().getId()), "댓글이 추천 되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}/cancelLike")
    public String deleteCommentVote(Principal principal, @PathVariable("id") Integer id) {
        Comment comment = this.commentService.getComment(id);
        SiteMember siteMember = this.memberService.getUser(principal.getName());
        this.commentService.deleteVote(comment, siteMember);
        return rq.redirect("/post/detail/%s".formatted(comment.getPost().getId()), "댓글 추천이 취소 되었습니다.");
    }
}
