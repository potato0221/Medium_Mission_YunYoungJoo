package com.ll.medium.domain.post.post.entity;


import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.post.comment.entity.Comment;
import com.ll.medium.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Builder
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Setter
@Getter
@ToString(callSuper = true)
public class Post extends BaseEntity {

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "Text")
    private String content;

    private LocalDateTime modifyDate;

    @ManyToOne
    private SiteMember author;

    @ManyToMany
    Set<SiteMember> voter;

    @Formula("(select count(*) from post_voter pv where pv.post_id = id)")
    private int voteCount;

    private boolean isPaid;
    private boolean isNotPublished;
    private Integer countByMember;
    private Integer viewCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

    public int getCommentCount() {
        return this.commentList.size();
    }
}
