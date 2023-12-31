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

@RequestMapping("/b/comment")
@RequiredArgsConstructor
@Controller
public class BlogCommentController {

    private final PostService postService;
    private final CommentService commentService;
    private final MemberService memberService;
    private final Rq rq;

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
        return rq.redirect("/b/%s/%s".formatted(username, id), "댓글이 작성 되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{username}/{postId}/modify/{commentId}")
    public String commentModify(CommentForm commentForm,
                                @PathVariable("username") String username,
                                @PathVariable("postId") Integer postId,
                                @PathVariable("commentId") Integer commentId,
                                Principal principal) {
        Comment comment = this.commentService.getComment(commentId);

        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            return rq.redirectIfAccessError("/post/access_denied", "수정 권한이 없는 댓글 입니다.");
        }

        commentForm.setContent(comment.getContent());
        return "comment/comment/comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{username}/{postId}/modify/{commentId}")
    public String commentModify(
            @Valid CommentForm commentForm,
            @PathVariable("username") String username,
            BindingResult bindingResult,
            @PathVariable("postId") Integer postId,
            @PathVariable("commentId") Integer commentId,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            return "comment/comment/comment_form";
        }

        Comment comment = this.commentService.getComment(commentId);

        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            return rq.redirectIfAccessError("/post/access_denied", "수정 권한이 없는 댓글 입니다.");
        }
        this.commentService.modify(comment, commentForm.getContent());
        return rq.redirect("/b/%s/%s".formatted(username, postId), "댓글이 수정 되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{username}/{postId}/delete/{commentId}") //username/postNum/delete/commentNum
    public String commentDelete(
            @PathVariable("username") String username,
            @PathVariable("postId") Integer postId,
            @PathVariable("commentId") Integer commentId) {

        Comment comment = this.commentService.getComment(commentId);

        if (!commentService.canDelete(rq.getMember(), comment)) {
            return rq.redirectIfAccessError("/post/access_denied", "삭제 권한이 없는 댓글 입니다.");
        }
        this.commentService.delete(comment);
        return rq.redirect("/b/%s/%s".formatted(username, postId), "댓글이 삭제 되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("{username}/{postId}/like/{commentId}")
    public String commentVote(Principal principal,
                              @PathVariable("username") String username,
                              @PathVariable("postId") Integer postId,
                              @PathVariable("commentId") Integer commentId) {
        SiteMember siteMember = this.memberService.getUser(principal.getName());
        Comment comment = this.commentService.getComment(commentId);
        this.commentService.vote(comment, siteMember);
        return rq.redirect("/b/%s/%s".formatted(username, postId), "댓글이 추천 되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{username}/{postId}/cancelLike/{commentId}")
    public String deleteCommentVote(Principal principal,
                                    @PathVariable("username") String username,
                                    @PathVariable("postId") Integer postId,
                                    @PathVariable("commentId") Integer commentId) {

        SiteMember siteMember2 = this.memberService.getUser(principal.getName());
        Comment comment = this.commentService.getComment(commentId);
        this.commentService.deleteVote(comment, siteMember2);
        return rq.redirect("/b/%s/%s".formatted(username, postId), "댓글 추천이 취소 되었습니다.");
    }


}
