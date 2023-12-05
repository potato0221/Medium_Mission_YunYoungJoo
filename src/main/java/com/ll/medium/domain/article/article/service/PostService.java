package com.ll.medium.domain.article.article.service;

import com.ll.medium.domain.article.article.entity.Post;
import com.ll.medium.domain.article.article.repository.PostRepository;
import com.ll.medium.domain.member.member.entity.SiteMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    public List<Post> getList(){
        return this.postRepository.findAll();
    }

    public Page<Post> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return this.postRepository.findAll(pageable);
    }

    public void create(String title, String content, SiteMember member) {
        Post post=new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreateDate(LocalDateTime.now());
        post.setAuthor(member);
        this.postRepository.save(post);
    }
}
