package com.ll.medium.domain.post.post.service;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.repository.PostRepository;
import com.ll.medium.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    public List<Post> getList(){
        return this.postRepository.findAll();
    }

    public Page<Post> getList(int page) {
        List<Sort.Order> sorts=new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.postRepository.findAll(pageable);
    }

    public Page<Post> getRecent30(int page){
        Pageable pageable = PageRequest.of(0,30,Sort.by(Sort.Order.desc("createDate")));
        return this.postRepository.findAll(pageable);
    }

    public Page<Post> getListByUsername(int page, SiteMember siteMember) {
        List<Sort.Order> sorts=new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.postRepository.findByAuthor(pageable,siteMember);
    }

    public Post getPost(Integer id){
        Optional<Post> post=this.postRepository.findById(id);
        if(post.isPresent()){
            return post.get();
        }else {
            throw new DataNotFoundException("question is not found");
        }
    }

    @Transactional
    public void create(String title, String content, SiteMember member) {
        Post post=new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreateDate(LocalDateTime.now());
        post.setAuthor(member);
        this.postRepository.save(post);
    }


    public boolean canModify(SiteMember member, Post post) {
        if(member==null) return false;
        return post.getAuthor().equals(member);
    }

    @Transactional
    public void modify(Post post, String title, String content) {
        post.setTitle(title);
        post.setContent(content);
        post.setModifyDate(LocalDateTime.now());
        this.postRepository.save(post);

    }
}
