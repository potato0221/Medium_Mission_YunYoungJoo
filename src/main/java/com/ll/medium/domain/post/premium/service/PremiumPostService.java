package com.ll.medium.domain.post.premium.service;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.post.premium.entity.PremiumPost;
import com.ll.medium.domain.post.premium.repository.PremiumPostRepository;
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
public class PremiumPostService {

    private final PremiumPostRepository premiumPostRepository;

    public List<PremiumPost> getList(){
        return this.premiumPostRepository.findAll();
    }

    public Page<PremiumPost> getList(int page) {
        List<Sort.Order> sorts=new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.premiumPostRepository.findAll(pageable);
    }

    public Page<PremiumPost> getRecent30(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return this.premiumPostRepository.findAll(PageRequest.of(0,30,Sort.by(Sort.Order.desc("createDate"))));
    }

    public PremiumPost getPost(Integer id){
        Optional<PremiumPost> premiumPost=this.premiumPostRepository.findById(id);
        if(premiumPost.isPresent()){
            return premiumPost.get();
        }else {
            throw new DataNotFoundException("question is not found");
        }
    }

    public void create(String title, String content, SiteMember member) {
        PremiumPost premiumPost=new PremiumPost();
        premiumPost.setTitle(title);
        premiumPost.setContent(content);
        premiumPost.setCreateDate(LocalDateTime.now());
        premiumPost.setAuthor(member);
        this.premiumPostRepository.save(premiumPost);
    }


}
