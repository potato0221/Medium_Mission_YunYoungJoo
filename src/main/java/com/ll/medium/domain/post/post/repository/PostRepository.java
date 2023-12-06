package com.ll.medium.domain.post.post.repository;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    Post findByTitle(String title);
    Post findByTitleAndContent(String title, String content);
    List<Post> findByTitleLike(String title);
    Page<Post> findAll(Pageable pageable);

    Page<Post> findByAuthor(Pageable pageable, SiteMember siteMember);

    @Query("SELECT p FROM Post p WHERE (p.author.id = :authorId AND p.isPublished = true) OR p.isPublished = false")
    Page<Post> findPublishedPostsByAuthorOrPublic(@Param("authorId") Long authorId, Pageable pageable);
}
