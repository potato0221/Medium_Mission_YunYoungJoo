package com.ll.medium.domain.post.post.entity;


import com.ll.medium.domain.member.member.entity.SiteMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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

    @Column(length=200)
    private String title;

    @Column(columnDefinition = "Text")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private SiteMember author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteMember> voter;

    public Post(String title, String content, LocalDateTime now){
        this.title=title;
        this.content=content;
        this.createDate=now;
    }
}
