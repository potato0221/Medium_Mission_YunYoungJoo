package com.ll.medium.domain.post.post.repository;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Integer>, PostRepositoryCustom {

    Page<Post> findAll(Pageable pageable);

    Page<Post> findByAuthor(Pageable pageable, SiteMember siteMember);


    @Query("SELECT p FROM Post p " +
            "WHERE (p.author.id = :authorId " +
            "AND p.isPublished = true) " +
            "OR p.isPublished = false"
    )
    Page<Post> findPublishedPostsByAuthorOrPublic(@Param("authorId") Long authorId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.countByMember = :id AND p.author = :siteMember")
    Post findByCountByMemberAndMember(SiteMember siteMember, Integer id);
}
