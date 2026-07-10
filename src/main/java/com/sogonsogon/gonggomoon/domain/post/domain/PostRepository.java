package com.sogonsogon.gonggomoon.domain.post.domain;


import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Post save(Post newPost);

    Optional<Post> findById(Long postId);

    Optional<Post> findByIdAndCreatedBy(Long postId, Long createdBy);

    List<Post> findAllById(Iterable<Long> ids);

    <S extends Post> List<S> saveAll(Iterable<S> entities);
}
