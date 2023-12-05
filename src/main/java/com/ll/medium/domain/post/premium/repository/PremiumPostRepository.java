package com.ll.medium.domain.post.premium.repository;

import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.premium.entity.PremiumPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PremiumPostRepository extends JpaRepository<PremiumPost, Integer> {

    Post findByTitle(String title);
    Post findByTitleAndContent(String title, String content);
    List<PremiumPost> findByTitleLike(String title);
    Page<PremiumPost> findAll(Pageable pageable);

}
