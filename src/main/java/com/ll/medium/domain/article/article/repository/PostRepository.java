package com.ll.medium.domain.article.article.repository;

import com.ll.medium.domain.article.article.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    Post findByTitle(String title);
    Post findByTitleAndContent(String title, String content);
    List<Post> findByTitleLike(String title);
    Page<Post> findAll(Pageable pageable);

}
