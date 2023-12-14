package com.ll.medium.domain.post.comment.service;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.post.comment.entity.Comment;
import com.ll.medium.domain.post.comment.repository.CommentRepository;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public void create(Post post, String content, SiteMember author) {

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setPost(post);
        comment.setAuthor(author);
        this.commentRepository.save(comment);
    }

    public Comment getComment(Integer id) {
        Optional<Comment> comment = this.commentRepository.findById(id);
        if (comment.isPresent()) {
            return comment.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }

    }

    @Transactional
    public void modify(Comment comment, String content) {
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }

    @Transactional
    public void delete(Comment comment) {
        this.commentRepository.delete(comment);
    }

    public boolean canDelete(SiteMember siteMember, Comment comment) {
        if (siteMember == null) return false;
        return comment.getAuthor().equals(siteMember);
    }


    @Transactional
    public void vote(Comment comment, SiteMember siteMember) {
        comment.getVoter().add(siteMember);
        this.commentRepository.save(comment);
    }

    @Transactional
    public void deleteVote(Comment comment, SiteMember siteMember) {
        comment.getVoter().remove(siteMember);
        this.commentRepository.save(comment);
    }

}
