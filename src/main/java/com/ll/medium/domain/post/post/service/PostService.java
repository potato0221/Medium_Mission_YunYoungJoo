package com.ll.medium.domain.post.post.service;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.repository.PostRepository;
import com.ll.medium.global.exception.DataNotFoundException;
import com.ll.medium.global.rq.Rq;
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
    private final Rq rq;

    public Page<Post> search(String kwTypes, String kw, Pageable pageable) {
        return postRepository.search(kwTypes, kw, pageable);
    }

    public Page<Post> getList(int page) {
        Long authorId = null;
        if (rq.isLogined()) {
            SiteMember siteMember = rq.getMember();
            authorId = siteMember.getId();
        }
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 9, Sort.by(sorts));
        return this.postRepository.findPublishedPostsByAuthorOrPublic(authorId, pageable);
    }


    public Page<Post> getRecent30() {
        Long authorId = null;
        if (rq.isLogined()) {
            SiteMember siteMember = rq.getMember();
            authorId = siteMember.getId();
        }
        Pageable pageable = PageRequest.of(0, 30, Sort.by(Sort.Order.desc("createDate")));
        return this.postRepository.findPublishedPostsByAuthorOrPublic(authorId, pageable);
    }

    public Page<Post> getListByUsername(int page, SiteMember siteMember) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Long authorId = siteMember.getId();
        Pageable pageable = PageRequest.of(page, 9, Sort.by(sorts));
        return this.postRepository.findByAuthor(pageable, siteMember);
    }

    public Page<Post> getOwnListByUsername(int page, SiteMember siteMember) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Long authorId = siteMember.getId();
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.postRepository.findByAuthor(pageable, siteMember);
    }


    public Post getPost(Integer id) {
        Optional<Post> post = this.postRepository.findById(id);
        if (post.isPresent()) {
            return post.get();
        } else {
            throw new DataNotFoundException("post is not found");
        }
    }

    @Transactional
    public void create(String title, String content, SiteMember member, boolean isPremium, boolean isPublished, Integer count, Integer viewCount) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreateDate(LocalDateTime.now());
        post.setAuthor(member);
        post.setPremium(isPremium);
        post.setPublished(isPublished);
        post.setCountByMember(count);
        post.setViewCount(viewCount);
        this.postRepository.save(post);
    }

    @Transactional
    public void modify(Post post, String title, String content, boolean isPremium, boolean isPublished) {
        post.setTitle(title);
        post.setContent(content);
        post.setModifyDate(LocalDateTime.now());
        post.setPremium(isPremium);
        post.setPublished(isPublished);
        this.postRepository.save(post);

    }

    @Transactional
    public void delete(Post post) {
        this.postRepository.delete(post);
    }


    public Post getPostByCountByMemberAndMember(SiteMember siteMember, Integer id) {
        return postRepository.findByCountByMemberAndMember(siteMember, id);
    }

    @Transactional
    public void vote(Post post, SiteMember siteMember) {
        post.getVoter().add(siteMember);
        this.postRepository.save(post);
    }

    @Transactional
    public void deleteVote(Post post, SiteMember siteMember) {
        post.getVoter().remove(siteMember);
        this.postRepository.save(post);
    }

    @Transactional
    public void incrementPostViewCount(Post post) {
        post.setViewCount(post.getViewCount() + 1);
        this.postRepository.save(post);
    }

}
