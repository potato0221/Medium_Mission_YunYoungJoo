package com.ll.medium.domain.post.post.repository;

import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.entity.QPost;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Post> search(List<String> kwTypes, String kw, String sort, Pageable pageable) {
        QPost post = QPost.post;
        BooleanBuilder builder = new BooleanBuilder();

        for (String kwType : kwTypes) {
            if (kwType.contains("title")) {
                builder.or(post.title.containsIgnoreCase(kw));
            }

            if (kwType.contains("content")) {
                builder.or(post.content.containsIgnoreCase(kw));
            }

            if (kwType.contains("author")) {
                builder.or(post.author.username.containsIgnoreCase(kw));
            }

        }

        // post.isNotPublished가 false인 경우만 검색 대상에 포함
        builder.and(post.isNotPublished.isFalse());

        JPAQuery<Post> query = jpaQueryFactory
                .selectFrom(post)
                .where(builder);

        if (sort.equals("asc")) {
            query.orderBy(post.id.asc());
        } else if (sort.equals("desc")) {
            query.orderBy(post.id.desc());
        } else if (sort.equals("voteCountDesc")) {
            query.orderBy(post.voteCount.desc(), post.id.desc());
        } else if (sort.equals("voteCountAsc")) {
            query.orderBy(post.voteCount.asc(), post.id.asc());
        } else if (sort.equals("viewCountDesc")) {
            query.orderBy(post.viewCount.desc(), post.id.desc());
        } else if (sort.equals("viewCountAsc")) {
            query.orderBy(post.viewCount.asc(), post.id.asc());
        }

        QueryResults<Post> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
