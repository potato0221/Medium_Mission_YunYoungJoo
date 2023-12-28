package com.ll.medium.domain.post.comment.entity;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.global.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Builder
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Setter
@Getter
@ToString(callSuper = true)
public class Comment extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime modifyDate;

    @ManyToOne
    private Post post;

    @ManyToMany
    Set<SiteMember> voter;

    @ManyToOne
    private SiteMember author;

}
