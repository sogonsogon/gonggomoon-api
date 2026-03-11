package com.sogonsogon.gonggomoon.domain.post.domain;

public interface PostRepository {

    boolean existsByUrl(String url);
}
