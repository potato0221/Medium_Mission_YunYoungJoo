package com.ll.medium.domain.post.post.entity;


import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.post.comment.entity.Comment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "Text")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private SiteMember author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteMember> voter;

    @Formula("(select count(*) from post_voter pv where pv.post_id = id)")
    private int voteCount;


    private boolean isPremium;
    private boolean isPublished;
    private Integer countByMember;
    private Integer viewCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

    public Post(String title, String content, LocalDateTime now) {
        this.title = title;
        this.content = content;
        this.createDate = now;
    }

}
