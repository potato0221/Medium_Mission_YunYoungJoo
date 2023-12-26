package com.ll.medium.domain.post.comment.repository;

import com.ll.medium.domain.post.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

}
