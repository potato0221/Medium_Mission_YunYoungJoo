package com.ll.medium.domain.post.post.service;

import com.ll.medium.domain.base.genFile.service.GenFileService;
import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
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
    private final GenFileService genFileService;
    private final MemberService memberService;

    public Page<Post> search(List<String> kwTypes, String kw, String sort, Pageable pageable) {
        return postRepository.search(kwTypes, kw, sort, pageable);
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
        return this.postRepository.findPublishedPosts(authorId, pageable);
    }


    public Page<Post> getRecent30() {
        Long authorId = null;
        if (rq.isLogined()) {
            SiteMember siteMember = rq.getMember();
            authorId = siteMember.getId();
        }
        Pageable pageable = PageRequest.of(0, 30, Sort.by(Sort.Order.desc("createDate")));
        return this.postRepository.findPublishedPosts(authorId, pageable);
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
    public Post create(SiteMember siteMember, String title, String content, SiteMember member, boolean isPaid, boolean isNotPublished, Long count) {
        siteMember.setCount(member.getCount() + 1);
        memberService.save(member);
        Post post = Post.builder()
                .title(title)
                .content(content)
                .author(member)
                .isPaid(isPaid)
                .isNotPublished(isNotPublished)
                .countByMember(count)
                .viewCount(0L)
                .build();
        this.postRepository.save(post);
        return post;
    }

    @Transactional
    public void modify(Post post, String title, String content, boolean isPaid, boolean isNotPublished) {
        post.setTitle(title);
        post.setContent(content);
        post.setPaid(isPaid);
        post.setNotPublished(isNotPublished);
        post.setModifyDate(LocalDateTime.now());
        this.postRepository.save(post);

    }

    @Transactional
    public void delete(Post post) {
        //해당 글 삭제 시 포함된 이미지도 같이 삭제
        genFileService.deleteGenfileByRelTypeAndRelId("post_" + post.getAuthor().getUsername(), post.getCountByMember());
        this.postRepository.delete(post);
    }


    public boolean canDelete(SiteMember siteMember, Post post) {
        if (siteMember == null) return false;
        return post.getAuthor().equals(siteMember);
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

    public boolean canLike(SiteMember member, Post post) {
        if (member == null) {
            return false;
        }
        return !post.getVoter().contains(member);
    }

    public boolean canCancelLike(SiteMember member, Post post) {
        if (member == null) {
            return false;
        }
        return post.getVoter().contains(member);
    }

    public Optional<Post> findById(Integer id) {
        return postRepository.findById(id);
    }
}
