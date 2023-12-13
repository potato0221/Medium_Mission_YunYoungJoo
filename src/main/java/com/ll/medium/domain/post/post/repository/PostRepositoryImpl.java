package com.ll.medium.domain.post.post.repository;

import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.entity.QPost;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Post> search(String kwTypes, String kw, Pageable pageable) {
        QPost post = QPost.post;
        BooleanBuilder builder = new BooleanBuilder();

        if (kwTypes.equals("title")) {
            builder.or(post.title.contains(kw));
        }

        if (kwTypes.equals("content")) {
            builder.or(post.content.contains(kw));
        }

        // post.isPublished가 false인 경우만 검색 대상에 포함
        builder.and(post.isPublished.isFalse());

        QueryResults<Post> results = jpaQueryFactory
                .selectFrom(post)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
