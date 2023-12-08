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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final PostService postService;
    private final CommentService commentService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createComment(Model model,
                                @PathVariable("id") Integer id,
                                @Valid CommentForm commentForm,
                                BindingResult bindingResult,
                                Principal principal
    ){
        Post post=this.postService.getPost(id);
        SiteMember siteMember=this.memberService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("post", post);
            return "post/post/post_detail";
        }

        this.commentService.create(post,commentForm.getContent(),siteMember);
        return String.format("redirect:/post/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String commentModify(CommentForm commentForm,
                                @PathVariable("id") Integer id,
                                Principal principal){

        Comment comment=this.commentService.getComment(id);
        if(!comment.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정 권한이 없습니다.");
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
            Principal principal){

        if(bindingResult.hasErrors()){
            return "comment/comment/comment_form";
        }
        Comment comment=this.commentService.getComment(id);

        if(!comment.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정 권한이 없습니다.");
        }

        this.commentService.modify(comment, commentForm.getContent());
        return String.format("redirect:/post/detail/%s",comment.getPost().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String commentDelete(Principal principal,
                                @PathVariable("id") Integer id){
        Comment comment=this.commentService.getComment(id);
        if(!comment.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/post/detail/%s",comment.getPost().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/like")
    public String commentVote(Principal principal,
                             @PathVariable("id") Integer id){
        Comment comment=this.commentService.getComment(id);
        SiteMember siteMember=this.memberService.getUser(principal.getName());
        this.commentService.vote(comment,siteMember);
        return String.format("redirect:/post/detail/%s", comment.getPost().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/cancelLike")
    public String deleteCommentVote(Principal principal, @PathVariable("id") Integer id) {
        Comment comment = this.commentService.getComment(id);
        SiteMember siteMember = this.memberService.getUser(principal.getName());
        this.commentService.deleteVote(comment, siteMember);
        return String.format("redirect:/post/detail/%s", comment.getPost().getId());
    }


}
