package com.ll.medium.domain.article.article.repository;

import com.ll.medium.domain.article.article.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {


}
