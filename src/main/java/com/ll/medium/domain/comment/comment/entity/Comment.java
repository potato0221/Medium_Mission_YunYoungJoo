package com.ll.medium.domain.comment.comment.entity;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.post.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @ManyToOne
    private Post post;

    @ManyToMany
    Set<SiteMember> voter;

    @ManyToOne
    private SiteMember author;

}
